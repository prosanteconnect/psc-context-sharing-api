package fr.ans.psc.context.sharing.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("PscContext")
public class PscContext {
    @Id
    @JsonProperty("psId")
    String psId;

    @JsonProperty(value = "schemaId", required = true)
    String schemaId;

    @JsonProperty(value = "bag", required = true)
    String bag;

    public PscContext() {
    }

    public PscContext(String psId, String schemaId, String bag) {
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

    public String getBag() {
        return bag;
    }

    public void setBag(String bag) {
        this.bag = bag;
    }
}
