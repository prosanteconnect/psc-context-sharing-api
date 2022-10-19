package fr.ans.psc.context.sharing.api.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.TestRedisConfiguration;
import fr.ans.psc.context.sharing.api.repository.PscContextRepository;
import fr.ans.psc.context.sharing.api.service.ShareService;
import fr.ans.psc.context.sharing.api.utils.MemoryAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureDataRedis
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
@ContextConfiguration(classes = ContextSharingApiApplication.class)
public class ShareControllerTest {
    protected MemoryAppender memoryAppender;
    protected final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PscContextRepository pscContextRepository;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        // LOG APPENDER
        Logger controllerLogger = (Logger) LoggerFactory.getLogger(ShareController.class);
        Logger serviceLogger = (Logger) LoggerFactory.getLogger(ShareService.class);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        controllerLogger.setLevel(Level.INFO);
        controllerLogger.addAppender(memoryAppender);
        serviceLogger.setLevel(Level.INFO);
        serviceLogger.addAppender(memoryAppender);
        memoryAppender.start();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

//    @Test
//    @DisplayName("should be rejected if token absent")
//    public void failedWithoutTokenTest() throws Exception {
//        ResultActions reqWithoutToken = mockMvc.perform(put("/share")
//        .header("Accept", "application/json")
//        .header("Content-Type", "application/json")
//        .content("{\"schemaId\":\"patient-info\", \"bag\":{\"nationalId\":\"123\"}"))
//                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//    }
}
