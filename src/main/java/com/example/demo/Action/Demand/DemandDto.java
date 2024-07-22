package com.example.demo.Action.Demand;

import com.example.demo.Action.Demand.DemandInterval.DemandIntervalDto;

import java.time.LocalDate;
import java.util.List;

public record DemandDto (
        Long demandId,
        LocalDate date,
        List<DemandIntervalDto> demandIntervals
){}
