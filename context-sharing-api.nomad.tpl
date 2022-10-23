job "psc-context-sharing" {
  datacenters = ["${datacenter}"]
  type = "service"
  namespace = "${nomad_namespace}"

  vault {
    policies = ["psc-ecosystem"]
    change_mode = "restart"
  }

  affinity {
    attribute = "$\u007Bnode.class\u007D"
    value = "standard"
  }

  group "psc-context-sharing" {
    count = "1"
    restart {
      attempts = 3
      delay = "60s"
      interval = "1h"
      mode = "fail"
    }

    network {
      port "http" {
        to = 8080
      }
    }

    task "api" {
      driver = "docker"

      config {
        image = "${artifact.image}:${artifact.tag}"
        ports = ["http"]
      }

      # env variables
      template {
        destination = "local/file.env"
        env = true
        data = <<EOH
PUBLIC_HOSTNAME={{ with secret "psc-ecosystem/${nomad_namespace}" }}{{ .Data.data.api_public_hostname }}{{ end }}
JAVA_TOOL_OPTIONS="-Xms256m -Xmx1g -XX:+UseG1GC -Dspring.config.location=/secrets/application.properties -Dlogging.level.fr.ans.psc=${log_level}"
EOH
      }

      #application.properties
      template {
        destination = "secrets/application.properties"
        change_mode = "restart"
        data = <<EOF
spring.application.name=psc-context-sharing-api
server.servlet.contextPath=/psc-context-sharing/api
logging.level.org.springframework=ERROR

#ProSanteConnect introspection Endpoint
{{ with secret "psc-ecosystem/${nomad_namespace}" }}
psc.url.introspection={{ .Data.data.introspection_url }}
psc.url.userinfo={{ .Data.data.userinfo_url }}
psc.clientID={{ .Data.data.psc_client_id }}
psc.clientSecret={{ .Data.data.psc_client_secret }}
{{ end }}

spring.redis.database=0
spring.redis.host={{ range service "${nomad_namespace}-redis-share-context" }}{{ .Address }}{{ end }}
spring.redis.port={{ range service "${nomad_namespace}-redis-share-context" }}{{ .Port }}{{ end }}
{{ with secret "psc-ecosystem/${nomad_namespace}" }}
spring.redis.username={{ .Data.data.redis_username }}
spring.redis.password={{ .Data.data.redis_password }}
{{ end }}
EOF
      }

      resources {
        cpu = 500
        memory = 1152
      }

      service {
        name = "$\u007BNOMAD_NAMESPACE\u007D-$\u007BNOMAD_JOB_NAME\u007D"
        tags = ["urlprefix-$\u007BPUBLIC_HOSTNAME\u007D/psc-context-sharing/"]
        port = "http"
        check {
          type = "http"
          path = "/psc-context-sharing/api/check"
          port = "http"
          interval = "30s"
          timeout = "2s"
          failures_before_critical = "3"
        }
      }
    }
  }
}
