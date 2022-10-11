package fr.ans.psc.context.sharing.api.service;

import fr.ans.psc.context.sharing.api.model.PscContext;

public class ShareService {

    public void putPsContext(String accessToken, PscContext pscContext) {
        // TODO : introspect PSC

        // TODO : check conformity of data bag

        // TODO : put to Redis

        // TODO : handle business exceptions
    }

    public PscContext getPscContext(String accessToken, String nationalId) {
        // TODO : introspect PSC

        // TODO : get from Redis

        // TODO : handle business exceptions

        return null;
    }
}
