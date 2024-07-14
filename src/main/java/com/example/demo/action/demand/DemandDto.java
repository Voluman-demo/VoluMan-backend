package com.example.demo.action.demand;

import com.example.demo.Interval.DemandIntervalDto;

import java.time.LocalDate;
import java.util.List;

public record DemandDto (
        Long demandId,
        LocalDate date,
        List<DemandIntervalDto> demandIntervals
){}
