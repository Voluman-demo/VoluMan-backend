package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Model.Language.LangISO;
import com.example.demo.Volunteer.Volunteer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

interface Actions {

    Errors addAction(SingleAction a);
    Errors remAction(ID id);
    Errors updAction(ID id, SingleAction a);
    Errors setMy(Volunteer v, ID d);
    Errors setRejected(Volunteer v, ID d);
    Errors setUndecided(Volunteer v, ID d);
    Errors setLanguage(LangISO lang, ID d);

    ArrayList<SingleAction> getAllActions(LangISO lang);
//    ArrayList<SingleAction> getMyActions(Long volunteerId);
//    ArrayList<SingleAction> getRejectedActions(Long volunteerId);
//    ArrayList<SingleAction> getUndecidedActions(Long volunteerId);


    default <T> ResponseEntity<?> isError(Errors error, T body) {
        return switch (error) {
            case SUCCESS -> (body != null)
                    ? ResponseEntity.ok(body)
                    : ResponseEntity.ok().build();
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case FAILURE -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }
}

