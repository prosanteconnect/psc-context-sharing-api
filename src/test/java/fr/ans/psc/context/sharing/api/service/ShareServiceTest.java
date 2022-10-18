package fr.ans.psc.context.sharing.api.service;

import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.TestRedisConfiguration;
import fr.ans.psc.context.sharing.api.exception.PscSchemaException;
import fr.ans.psc.context.sharing.api.model.PscContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class ShareServiceTest {

    @Autowired
    private ShareService shareService;

    @Test
    @DisplayName("should accept valid json schema")
    public void shouldAcceptValidJsonSchema() {
        PscContext pscContext = new PscContext("123", "patient-info", "{\"ps\":{\"nationalId\":\"123\"}}");
        try {
            shareService.putPsContext(pscContext);
        } catch (PscSchemaException e) {
            e.printStackTrace();
            fail("Should not have thrown PscSchemaException");
        } catch (Exception ignored) { }
    }

    @Test
    @DisplayName("should reject invalid json schema")
    public void shouldRejectInvalidJsonSchema() {
        PscContext pscContext = new PscContext("123", "patient-info", "{\"ps\":{\"nationalId\":\"123\",\"invalid_property\":\"something\"}}");
        PscSchemaException exception = assertThrows(PscSchemaException.class, () -> shareService.putPsContext(pscContext));
    }

    //TODO put in cache (mock)

    //TODO get from cache (mock)

    //TODO
}
