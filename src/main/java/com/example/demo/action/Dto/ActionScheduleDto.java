package com.example.demo.action.Dto;

import com.example.demo.action.demand.DemandDto;

import java.util.List;

public record ActionScheduleDto(
        Long actionId,
        String heading,
        String description,
        List<DemandDto> demands
){}
