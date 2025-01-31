package com.example.demo.Action.ActionDemand.Dto;

import com.example.demo.Action.ActionDemand.ActionDemandIntervalRequest;
import lombok.Data;

import java.util.Set;

@Data
public class ActionNeedRequest {
    private Long leaderId;
    private Long volunteerId;
    private ActionDemandRequest actionDemand;
    private Set<ActionDemandIntervalRequest> actionDemandIntervals;
}