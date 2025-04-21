package com.fitness.activityService.dto;

import lombok.Builder;
import lombok.Data;
import com.fitness.activityService.model.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ActivityRequest {
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;

}
