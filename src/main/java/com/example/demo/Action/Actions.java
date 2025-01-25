package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;

import java.time.LocalDate;
import java.util.ArrayList;

public interface Actions {
    LocalDate noDate = LocalDate.of(1970, 1, 1);

    ID create();

    Errors remove(ID aId);

    Errors setBeg(ID aId, LocalDate beginDate);

    Errors setEnd(ID aId, LocalDate endDate);

    LocalDate getBeg(ID aId);

    LocalDate getEnd(ID aId);

    Errors setDesc(ID aId, Lang l, Description desc);

    Errors remDesc(ID aId, Lang l);

    Description getDesc(ID aId, Lang l);

    ArrayList<ID> getAllIds();

    ArrayList<Description> getAllDesc(Lang l);

    Errors setStronglyMine(User u, ID aId);

    Errors setWeaklyMine(User u, ID aId);

    Errors setRejected(User u, ID aId);

    Errors setUndecided(User u, ID aId);

    ArrayList<Description> getStronglyMine(User u);

    ArrayList<Description> getWeaklyMine(User u);

    ArrayList<Description> getRejected(User u);

    ArrayList<Description> getUndecided(User user);

    Errors isError();
}