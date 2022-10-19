package fr.ans.psc.context.sharing.api.service;

import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.exception.PscSchemaException;
import fr.ans.psc.context.sharing.api.model.PscContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class ShareServiceTest {

    @Autowired
    private ShareService shareService;

    @Test
    @DisplayName("should accept valid json schema")
    public void shouldAcceptValidJsonSchemaTest() {
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
    public void shouldRejectInvalidJsonSchemaTest() {
        PscContext pscContext = new PscContext("123", "patient-info", "{\"ps\":{\"nationalId\":\"123\",\"invalid_property\":\"something\"}}");
        assertThrows(PscSchemaException.class, () -> shareService.putPsContext(pscContext));
    }

    @Test
    @DisplayName("should reject unknown json schema")
    public void shouldRejectUnknownJsonSchemaTest(){
        PscContext pscContext = new PscContext("123", "unknown-schema", "{\"ps\":{\"nationalId\":\"123\"}}");
        assertThrows(PscSchemaException.class, () -> shareService.putPsContext(pscContext));
    }
}
