package com.example.demo.Action.ActionDemand;

import com.example.demo.Action.ActionDemand.ActionDemandInterval.ActionDemandInterval;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateNeedDto {
    private Long volunteerId;
    private ActionDemand actionDemand;
    private Set<ActionDemandInterval> actionDemandIntervals;
}