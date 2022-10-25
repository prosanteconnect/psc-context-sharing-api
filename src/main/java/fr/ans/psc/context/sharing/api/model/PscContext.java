package fr.ans.psc.context.sharing.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// default TimeUnit is seconds
@RedisHash(value = "PscContext", timeToLive = 900)
public class PscContext {
    @Id
    @JsonProperty("psId")
    String psId;

    @JsonProperty(value = "schemaId", required = true)
    String schemaId;

    @JsonProperty(value = "bag", required = true)
    Object bag;

    public PscContext() {
    }

    public PscContext(String psId, String schemaId, Object bag) {
        this.psId = psId;
        this.schemaId = schemaId;
        this.bag = bag;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public Object getBag() {
        return bag;
    }

    public void setBag(Object bag) {
        this.bag = bag;
    }
}
