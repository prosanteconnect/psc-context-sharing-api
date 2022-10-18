package fr.ans.psc.context.sharing.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("PscContext")
public class PscContext {
    @Id
    String psId;

    String schemaId;

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
