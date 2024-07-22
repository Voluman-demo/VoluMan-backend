package com.example.demo.Action.ActionDto;

import com.example.demo.Action.Demand.DemandDto;

import java.util.List;

public record ActionScheduleDto(
        Long actionId,
        String heading,
        String description,
        List<DemandDto> demands
){}
