package fr.ans.psc.context.sharing.api.repository;

import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.TestRedisConfiguration;
import fr.ans.psc.context.sharing.api.model.PscContext;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import redis.embedded.RedisServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureDataRedis
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class PsContextRepositoryTest {

    @Autowired
    private PscContextRepository ctxRepository;

    @Test
    public void shouldSavePscContext_toRedis() {
        final PscContext pscContext = new PscContext("1", "schema", "shared_data");
        final PscContext savedPscContext = ctxRepository.save(pscContext);

        assertNotNull(savedPscContext);
        assertEquals(pscContext.getBag(), savedPscContext.getBag());
        assertEquals("shared_data", savedPscContext.getBag());
    }
}
