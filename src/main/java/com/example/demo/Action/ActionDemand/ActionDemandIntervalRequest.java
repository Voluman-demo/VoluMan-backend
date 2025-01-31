package com.example.demo.Action.ActionDemand;

import lombok.Data;

@Data
public class ActionDemandIntervalRequest {
    private String startTime;
    private String endTime;
    private Long needMin;
    private Long needMax;
}
