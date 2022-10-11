package fr.ans.psc.context.sharing.api.model;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("PscContext")
public class PscContext {
    String schemaId;
    Object bag;

    public PscContext() {
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
