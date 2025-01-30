package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.User.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface Actions {
    LocalDate noDate = LocalDate.of(1970, 1, 1);

    Long create();

    Errors remove(Long aId);

    Errors setBeg(Long aId, LocalDate beginDate);

    Errors setEnd(Long aId, LocalDate endDate);

    LocalDate getBeg(Long aId);

    LocalDate getEnd(Long aId);

    Errors setDesc(Long aId, Lang l, Description desc);

    Errors remDesc(Long aId, Lang l);

    Description getDesc(Long aId, Lang l);

    ArrayList<Long> getAllIds();

    ArrayList<Description> getAllDesc(Lang l);

    Errors setPref(Long aId, String pref, Long volId);

    List<Description> getPref(String pref , Long volId);

    Errors isError();
}