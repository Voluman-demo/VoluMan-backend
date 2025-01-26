package com.example.demo.Action.ActionDto;

import com.example.demo.Action.Demand.DemandDto;
import com.example.demo.Model.ID;

import java.util.List;

public record ActionScheduleDto(
        ID actionId,
        String heading,
        String description,
        List<DemandDto> demands
){}
