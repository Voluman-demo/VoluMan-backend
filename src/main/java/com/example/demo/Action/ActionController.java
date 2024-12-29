package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Model.Language.LangISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping("")
    public ResponseEntity<?> addAction(@RequestBody SingleAction request) {
        Errors result = actionService.addAction(request);
        return actionService.isError(result, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAction(@PathVariable int id) {
        Errors result = actionService.remAction(new ID(id));
        return actionService.isError(result, null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAction(@PathVariable int id, @RequestBody SingleAction request) {
        Errors result = actionService.updAction(new ID(id), request);
        return actionService.isError(result, null);
    }

    @GetMapping("/all/{lang}")
    public ResponseEntity<?> getAllActions(@PathVariable LangISO lang) {
        ArrayList<SingleAction> actions = actionService.getAllActions(lang);
        return actionService.isError(actions.isEmpty() ? Errors.NOT_FOUND : Errors.SUCCESS, actions);
    }

//    @GetMapping("/my/{volunteerId}")
//    public ResponseEntity<?> getMyActions(@PathVariable Long volunteerId) {
//        ArrayList<SingleAction> actions = actionService.getMyActions(volunteerId);
//        return actionService.isError(actions.isEmpty() ? Errors.NOT_FOUND : Errors.SUCCESS, actions);
//    }
//
//    @GetMapping("/rejected/{volunteerId}")
//    public ResponseEntity<?> getRejectedActions(@PathVariable Long volunteerId) {
//        ArrayList<SingleAction> actions = actionService.getRejectedActions(volunteerId);
//        return actionService.isError(actions.isEmpty() ? Errors.NOT_FOUND : Errors.SUCCESS, actions);
//    }
//
//    @GetMapping("/undecided/{volunteerId}")
//    public ResponseEntity<?> getUndecidedActions(@PathVariable Long volunteerId) {
//        ArrayList<SingleAction> actions = actionService.getUndecidedActions(volunteerId);
//        return actionService.isError(actions.isEmpty() ? Errors.NOT_FOUND : Errors.SUCCESS, actions);
//    }
}
