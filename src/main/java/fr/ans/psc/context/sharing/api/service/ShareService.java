package fr.ans.psc.context.sharing.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import fr.ans.psc.context.sharing.api.exception.PscCacheException;
import fr.ans.psc.context.sharing.api.exception.PscSchemaException;
import fr.ans.psc.context.sharing.api.model.PscContext;
import fr.ans.psc.context.sharing.api.repository.PscContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ShareService {

    @Value("${schemas.file.repository}")
    private String schemasFileRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PscContextRepository pscContextRepository;

    public PscContext putPsContext(PscContext pscContext) throws PscCacheException, PscSchemaException {
        validateSchemaConformity(pscContext);
        PscContext saved;
        try {
            log.debug("Trying to save entry for key {} in Redis server...", pscContext.getPsId());
            saved = pscContextRepository.save(pscContext);
            log.debug("Entry for key {} successfully saved", pscContext.getPsId());
        } catch (Exception e) {
            log.error("Error occurred while requesting Redis server", e);
            throw new PscCacheException();
        }
        return saved;
    }

    public PscContext getPscContext(String nationalId) throws PscCacheException {
        Optional<PscContext> optionalContext;

        try {
            log.debug("requesting Redis server for key {}...", nationalId);
            optionalContext = pscContextRepository.findById(nationalId);
            log.debug("response received from Redis server for key {}", nationalId);
        } catch (Exception e) {
            log.error("Error occurred while requesting Redis server", e);
            throw new PscCacheException();
        }

        if (optionalContext.isEmpty()) {
            log.debug("No entry for key {}", nationalId);
            throw new PscCacheException(HttpStatus.NOT_FOUND);
        }
        return optionalContext.get();
    }

    private void validateSchemaConformity(PscContext pscContext) throws PscSchemaException {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            File jsonSchemaFile = new File(schemasFileRepository, pscContext.getSchemaId() + ".json");
            System.out.println(jsonSchemaFile.getAbsolutePath());
            InputStream inputStream = new FileInputStream(jsonSchemaFile);
            JsonSchema jsonSchema = factory.getSchema(inputStream);

            String jsonString = mapper.writeValueAsString(pscContext.getBag());
            JsonNode jsonNode = mapper.readTree(jsonString);

            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            if (!errors.isEmpty()) {
                log.error("Json-schema validation failed");
                throw new PscSchemaException();
            }
        } catch (JsonProcessingException | FileNotFoundException e) {
            log.error(e instanceof JsonProcessingException ? "Submitted json-schema has format errors" : "Unknown schema submitted");
            throw new PscSchemaException();
        }
    }
}
