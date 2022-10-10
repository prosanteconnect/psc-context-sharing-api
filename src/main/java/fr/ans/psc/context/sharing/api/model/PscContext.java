package fr.ans.psc.context.sharing.api.model;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("PscContext")
public class PscContext {
    String pscId;
    Object bag;
}
