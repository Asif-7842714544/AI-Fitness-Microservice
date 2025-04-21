package com.fitness.activityService.controller;


import com.fitness.activityService.dto.ActivityRequest;
import com.fitness.activityService.dto.ActivityResponse;
import com.fitness.activityService.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest activityRequest){
        return ResponseEntity.ok(activityService.trackActivity(activityRequest));

    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("x-user-Id")String userId){
        return ResponseEntity.ok(activityService.getUserActivities(userId));

    }
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getUserActivityById(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));

    }
}
