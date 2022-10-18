package fr.ans.psc.context.sharing.api.controller;

import fr.ans.psc.context.sharing.api.model.PscContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
public class ShareController {

    @GetMapping("/{psId}")
    public ResponseEntity<PscContext> getPsContextFromCache(@PathVariable String psId) {
        //TODO check token presence. if not return forbidden

        //TODO call service, handle ex and return PscContext
        return null;
    }

    @PutMapping("/{psId}")
    public ResponseEntity<PscContext> putPscContextInCache(@PathVariable String psId) {
        //TODO check token presence. if not return forbidden

        //TODO call service, handle ex and return PscContext
        return null;
    }
}
