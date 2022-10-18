package fr.ans.psc.context.sharing.api.service;

import fr.ans.psc.context.sharing.api.exception.PscMissingAccessTokenException;
import fr.ans.psc.context.sharing.api.exception.PscUnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class PscAuthService {

    public String introspectPscAccesToken(String accesToken) throws PscMissingAccessTokenException, PscUnauthorizedException {
        // TODO check access token presence

        // TODO introspect -> continue or throw exception

        // TODO getUserInfos -> return nationalId
        return null;
    }
}
