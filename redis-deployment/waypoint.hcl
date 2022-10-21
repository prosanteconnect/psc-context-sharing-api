project = "${workspace.name}/redis-share"

labels = {
  "domaine" = "psc"
}

runner {
  enabled = true
  profile = "sharecontext-${workspace.name}"
  data_source "git" {
    url = "https://github.com/prosanteconnect/psc-context-sharing-api.git"
    ref = "${workspace.name}"
    path = "redis-deployment"
    ignore_changes_outside_path = true
  }
  poll {
    enabled = false
  }
}

app "psc/share-context/redis" {
  build {
    use "docker-pull" {
      image = var.image
      tag = var.tag
    }
    registry {
      use "docker" {
        image = "prosanteconnect/psc-elasticsearch"
        tag = var.tag
        username = var.registry_username
        password = var.registry_password
        local = true
      }
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/redis.nomad.tpl", {
        datacenter = var.datacenter
        image = var.image
        tag = var.tag
        nomad_namespace = var.nomad_namespace
      })
    }
  }
}

variable "datacenter" {
  type = string
  default = ""
  env = [
    "NOMAD_DATACENTER"]
}

variable "nomad_namespace" {
  type = string
  default = ""
  env = [
    "NOMAD_NAMESPACE"]
}

variable "registry_username" {
  type = string
  default = ""
  env = [
    "REGISTRY_USERNAME"]
  sensitive = true
}

variable "registry_password" {
  type = string
  default = ""
  env = [
    "REGISTRY_PASSWORD"]
  sensitive = true
}

variable "image" {
  type = string
  default = "redis"
}

variable "tag" {
  type = string
  default = "latest"
}
