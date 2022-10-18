package fr.ans.psc.context.sharing.api.model;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("PscContext")
public class PscContext {
    String schemaId;
    Object bag;

    public PscContext() {
    }

    public PscContext(String schemaId, Object bag) {
        this.schemaId = schemaId;
        this.bag = bag;
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
