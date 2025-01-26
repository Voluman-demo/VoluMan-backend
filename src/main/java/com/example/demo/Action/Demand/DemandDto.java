package com.example.demo.Action.Demand;

import com.example.demo.Action.Demand.DemandInterval.DemandIntervalDto;
import com.example.demo.Model.ID;

import java.time.LocalDate;
import java.util.List;

public record DemandDto (
        ID demandId,
        LocalDate date,
        List<DemandIntervalDto> demandIntervals
){}
