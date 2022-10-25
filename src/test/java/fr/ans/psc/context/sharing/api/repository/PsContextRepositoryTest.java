package fr.ans.psc.context.sharing.api.repository;

import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.TestRedisConfiguration;
import fr.ans.psc.context.sharing.api.model.PscContext;
import fr.ans.psc.context.sharing.api.utils.PscContextBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureDataRedis
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class PsContextRepositoryTest {

    @Autowired
    private PscContextRepository ctxRepository;

    @Test
    public void shouldSavePscContext_toRedis() {
        final PscContext pscContext = new PscContextBuilder().withPsId("1").withSchemaId("schema").withSimpleBag("shared_data").build();
        final PscContext savedPscContext = ctxRepository.save(pscContext);

        assertNotNull(savedPscContext);
        assertEquals(pscContext.getBag(), savedPscContext.getBag());
        assertEquals("shared_data", savedPscContext.getBag());

        PscContext foundCtx = ctxRepository.findById("1").get();
        assertEquals("shared_data", foundCtx.getBag());
    }

    @Test
    @DisplayName("should update context")
    public void shouldReplacePscContext() {
        PscContext firstContext = new PscContextBuilder().withPsId("1").withSchemaId("schema").withSimpleBag("first_version").build();
        PscContext firstSaved = ctxRepository.save(firstContext);

        PscContext secondContext = new PscContextBuilder().withPsId("1").withSchemaId("schema").withSimpleBag("second_version").build();
        PscContext secondSaved = ctxRepository.save(secondContext);

        assertNotEquals(firstSaved, secondSaved);
        assertNotEquals(firstSaved.getBag(), secondSaved.getBag());
        assertEquals("second_version", secondSaved.getBag());
        assertEquals("second_version", ctxRepository.findById("1").get().getBag());
    }

    // this test should only be enabled manually :
    // Spring Data Redis sets TimeToLive via @RedisHash annotation of model class
    //
    // I don't know how to override this value or set it dynamically
    // and I don't want to wait the current required duration
    @Test
    @DisplayName("should not be available after TTL")
    @Disabled
    public void shouldFlushAfterTtlTest() throws InterruptedException {
        final PscContext pscContext = new PscContextBuilder().withPsId("1").withSchemaId("schema").withSimpleBag("shared_data").build();
        final PscContext savedPscContext = ctxRepository.save(pscContext);

        assertTrue(ctxRepository.existsById("1"));
        Thread.sleep(6000);
        assertFalse(ctxRepository.existsById("1"));
    }
}
