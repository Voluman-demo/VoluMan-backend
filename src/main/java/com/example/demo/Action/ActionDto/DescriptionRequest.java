package com.example.demo.Action.ActionDto;

import com.example.demo.Action.Lang;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DescriptionRequest {
    private LocalDate begin;
    private LocalDate end;
    private Lang lang;
    private boolean valid;
    private String fullName;
    private String shortName;
    private String place;
    private String address;
    private String description;
    private String hours;
    private List<RoleRequest> roles;
}