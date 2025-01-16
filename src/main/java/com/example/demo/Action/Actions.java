package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;

import java.time.LocalDate;
import java.util.ArrayList;

public interface Actions {
    LocalDate noDate = LocalDate.of(1970, 1, 1);

    ID create();

    Errors remove(ID actionId);

    Errors setBeg(ID actionId, LocalDate beginDate);

    Errors setEnd(ID actionId, LocalDate endDate);

    LocalDate getBeg(ID actionId);

    LocalDate getEnd(ID actionId);

    Errors setDesc(ID actionId, Lang language, Description description);

    Errors remDesc(ID actionId, Lang language);

    Description getDesc(ID actionId, Lang language);

    ArrayList<ID> getAllIds();

    ArrayList<Description> getAllDesc(Lang language);

    Errors setStronglyMine(User user, ID actionId);

    Errors setWeaklyMine(User user, ID actionId);

    Errors setRejected(User user, ID actionId);

    Errors setUndecided(User user, ID actionId);

    ArrayList<Description> getStronglyMine(User user);

    ArrayList<Description> getWeaklyMine(User user);

    ArrayList<Description> getRejected(User user);

    ArrayList<Description> getUndecided(User user);

    Errors isError();
}