package fr.ans.psc.context.sharing.api.service;

import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.exception.PscCacheException;
import fr.ans.psc.context.sharing.api.exception.PscSchemaException;
import fr.ans.psc.context.sharing.api.model.PscContext;
import fr.ans.psc.context.sharing.api.repository.PscContextRepository;
import fr.ans.psc.context.sharing.api.utils.PscContextBuilder;
import io.lettuce.core.RedisException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class ShareServiceTest {

    @Autowired
    private ShareService shareService;

    @MockBean
    private PscContextRepository repository;

    @Test
    @DisplayName("should accept valid json schema")
    public void shouldAcceptValidJsonSchemaTest() {
        PscContext pscContext = new PscContextBuilder().withPsId("123").withSchemaId("patient-info")
                .withPatientInfoBag().withPsNationalId("123").build();
        try {
            shareService.putPsContext(pscContext);
        } catch (PscSchemaException e) {
            e.printStackTrace();
            fail("Should not have thrown PscSchemaException");
        } catch (Exception ignored) { }
    }

    @Test
    @DisplayName("should reject invalid json schema")
    public void shouldRejectInvalidJsonSchemaTest() {
        PscContext pscContext = new PscContextBuilder().withPsId("123").withSchemaId("patient-info")
                .withUnAckedBag().build();
        assertThrows(PscSchemaException.class, () -> shareService.putPsContext(pscContext));
    }

    @Test
    @DisplayName("should reject unknown json schema")
    public void shouldRejectUnknownJsonSchemaTest(){
        PscContext pscContext = new PscContextBuilder().withPsId("123").withSchemaId("unknown-schema")
                .withPatientInfoBag().withPsNationalId("123").build();
        assertThrows(PscSchemaException.class, () -> shareService.putPsContext(pscContext));
    }

    @Test
    @DisplayName("should handle Redis Error")
    public void shouldHandleRedisErrorTest() {
        PscContext pscContext = new PscContextBuilder().withPsId("123").withSchemaId("patient-info")
                .withPatientInfoBag().withPsNationalId("123").build();
        Mockito.when(repository.save(pscContext)).thenThrow(RedisException.class);
        assertThrows(PscCacheException.class, () -> shareService.putPsContext(pscContext));
    }
}
