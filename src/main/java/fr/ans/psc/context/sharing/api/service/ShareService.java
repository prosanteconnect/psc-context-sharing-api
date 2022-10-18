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
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class ShareService {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PscContextRepository pscContextRepository;

    public PscContext putPsContext(PscContext pscContext) throws PscCacheException, PscSchemaException {
        // TODO : check conformity of data bag
        validateSchemaConformity(pscContext);

        // TODO : put to Redis
        try {
            pscContextRepository.save(pscContext);
        } catch (Exception e) {
            throw new PscCacheException();
        }

        return null;
    }

    public PscContext getPscContext(String nationalId) throws PscCacheException {
        // TODO : get from Redis
        PscContext pscContext;
        try {
            pscContext = pscContextRepository.findById(nationalId).get();
        } catch (Exception e) {
            throw new PscCacheException();
        }
        return pscContext;
    }

    private void validateSchemaConformity(PscContext pscContext) throws PscSchemaException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(
                PscContext.class.getResourceAsStream("/" + pscContext.getSchemaId() + ".json")
        );

        try {
            JsonNode jsonNode = mapper.readTree(pscContext.getBag());

            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            if (!errors.isEmpty()) {
                log.error("Json-schema validation failed");
                throw new PscSchemaException();
            }
        } catch (JsonProcessingException e) {
            log.error("Submitted json-schema has format errors");
            throw new PscSchemaException();
        }

    }
}
