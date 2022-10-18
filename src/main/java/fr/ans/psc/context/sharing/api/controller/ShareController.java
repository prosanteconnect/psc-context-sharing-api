package fr.ans.psc.context.sharing.api.controller;

import fr.ans.psc.context.sharing.api.exception.PscCacheException;
import fr.ans.psc.context.sharing.api.exception.PscMissingAccessTokenException;
import fr.ans.psc.context.sharing.api.exception.PscUnauthorizedException;
import fr.ans.psc.context.sharing.api.model.PscContext;
import fr.ans.psc.context.sharing.api.service.PscAuthService;
import fr.ans.psc.context.sharing.api.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private PscAuthService authService;

    @Autowired
    private ShareService shareService;

    @GetMapping("/{psId}")
    public ResponseEntity<PscContext> getPsContextFromCache(@PathVariable String psId) {
        //TODO check token presence. if not return forbidden
        String accessToken = "accesToken";
        String nationalId;
        PscContext pscContext;

        try {
            nationalId = authService.introspectPscAccesToken(accessToken);
        } catch (PscMissingAccessTokenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (PscUnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //TODO call service, handle ex and return PscContext
        try {
            pscContext = shareService.getPscContext(nationalId);
        } catch (PscCacheException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(pscContext, HttpStatus.OK);
    }

    @PutMapping("/{psId}")
    public ResponseEntity<PscContext> putPscContextInCache(@RequestBody PscContext pscContext) {
        //TODO check token presence. if not return forbidden
        String accessToken = "accesToken";
        String nationalId;
        PscContext savedContext;

        try {
            nationalId = authService.introspectPscAccesToken(accessToken);
        } catch (PscMissingAccessTokenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (PscUnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //TODO call service, handle ex and return PscContext
        try {
            pscContext.setPsId(nationalId);
            savedContext = shareService.putPsContext(pscContext);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(savedContext, HttpStatus.OK);
    }
}
