package fr.ans.psc.context.sharing.api.utils;

import fr.ans.psc.context.sharing.api.model.Patient;
import fr.ans.psc.context.sharing.api.model.PatientInfo;
import fr.ans.psc.context.sharing.api.model.Ps;
import fr.ans.psc.context.sharing.api.model.PscContext;
import org.springframework.security.core.parameters.P;

import java.util.HashMap;
import java.util.Map;

public class PscContextBuilder {

    private PscContext pscContext;

    public PscContextBuilder() {
        this.pscContext = new PscContext();
    }

    public PscContextBuilder withPsId(String psId) {
        this.pscContext.setPsId(psId);
        return this;
    }

    public PscContextBuilder withSchemaId(String schemaId) {
        this.pscContext.setSchemaId(schemaId);
        return this;
    }

    public PscContextBuilder withPatientInfoBag() {
        Ps ps = new Ps();
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setPs(ps);
        this.pscContext.setBag(patientInfo);
        return this;
    }

    public PscContextBuilder withUnAckedBag() {
        Map<String, String> map = new HashMap<>();
        map.put("unacked_property", "unacked_value");
        this.pscContext.setBag(map);
        return this;
    }

    public PscContextBuilder withSimpleBag(String bag) {
        this.pscContext.setBag(bag);
        return this;
    }

    public PscContextBuilder withPsNationalId(String nationalId) {
        PatientInfo patientInfo = (PatientInfo) this.pscContext.getBag();
        patientInfo.getPs().setNationalId(nationalId);
        return this;
    }

    public PscContext build() {
        return this.pscContext;
    }
}
