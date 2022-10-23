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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ShareService {

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
            log.error("Error occurred while requesting Redis server");
            throw new PscCacheException();
        }
        return saved;
    }

    public PscContext getPscContext(String nationalId) throws PscCacheException {
        PscContext pscContext;
        try {
            log.debug("requesting Redis server for key {}...", nationalId);
            Optional<PscContext> optional = pscContextRepository.findById(nationalId);
            if (optional.isPresent()) {
                pscContext = optional.get();
            } else {
                log.debug("No entry for key {}", nationalId);
                throw new PscCacheException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while requesting Redis server");
            throw new PscCacheException();
        }
        return pscContext;
    }

    private void validateSchemaConformity(PscContext pscContext) throws PscSchemaException {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            JsonSchema jsonSchema = factory.getSchema(
                    PscContext.class.getResourceAsStream("/" + pscContext.getSchemaId() + ".json")
            );
            JsonNode jsonNode = mapper.readTree(pscContext.getBag());

            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            if (!errors.isEmpty()) {
                log.error("Json-schema validation failed");
                throw new PscSchemaException();
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.error(e instanceof JsonProcessingException ? "Submitted json-schema has format errors" : "Unknown schema submitted");
            throw new PscSchemaException();
        }
    }
}
