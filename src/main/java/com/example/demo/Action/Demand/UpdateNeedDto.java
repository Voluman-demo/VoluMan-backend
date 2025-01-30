package com.example.demo.Action.Demand;

import com.example.demo.Action.Demand.DemandInterval.DemandInterval;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateNeedDto {
    private Long volunteerId;
    private Demand demand;
    private Set<DemandInterval> demandIntervals;
}