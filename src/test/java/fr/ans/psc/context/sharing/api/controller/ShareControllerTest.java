package fr.ans.psc.context.sharing.api.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.gson.Gson;
import fr.ans.psc.context.sharing.api.ContextSharingApiApplication;
import fr.ans.psc.context.sharing.api.TestRedisConfiguration;
import fr.ans.psc.context.sharing.api.model.PscContext;
import fr.ans.psc.context.sharing.api.repository.PscContextRepository;
import fr.ans.psc.context.sharing.api.service.ShareService;
import fr.ans.psc.context.sharing.api.utils.MemoryAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @RegisterExtension
    static WireMockExtension httpMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
                    .usingFilesUnderClasspath("wiremock/api"))
            .configureStaticDsl(true).build();

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry propertiesRegistry) {
        propertiesRegistry.add("psc.url.introspection", () -> httpMockServer.baseUrl());
        propertiesRegistry.add("psc.url.userinfo", () -> httpMockServer.baseUrl());
        propertiesRegistry.add("psc.clientID", () -> "client-id");
        propertiesRegistry.add("psc.clientSecret", () -> "client-secret");
    }

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

    @Test
    @DisplayName("put context should be rejected if token absent")
    public void failedWithoutTokenTest() throws Exception {
        ResultActions reqWithoutToken = mockMvc.perform(put("/share")
                .header("Accept", "application/json")
                .contentType("application/json")
                .content("{\"schemaId\":\"patient-info\",\"bag\":\"{\\\"nationalId\\\":\\\"123\\\"}\"}")
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("invalid token should failed")
    public void invalidTokenFailedTest() {
        httpMockServer.stubFor(WireMock.post("/introspect").willReturn(aResponse().withStatus(200).withBody("")));
    }
}
