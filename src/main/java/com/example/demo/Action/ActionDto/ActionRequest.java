package com.example.demo.Action.ActionDto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ActionRequest {
    private LocalDate begin;
    private LocalDate end;
    private Long leaderId;
    private List<DescriptionRequest> descr;
}