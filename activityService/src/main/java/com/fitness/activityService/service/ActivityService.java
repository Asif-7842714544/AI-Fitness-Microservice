package com.fitness.activityService.service;

import com.fitness.activityService.dto.ActivityRequest;
import com.fitness.activityService.dto.ActivityResponse;
import com.fitness.activityService.repository.ActivityRepo;
import lombok.RequiredArgsConstructor;
import com.fitness.activityService.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepo activityRepo;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;


    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        log.info("inside trackActivity");
        boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + activityRequest.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();
        Activity savedActivity = activityRepo.save(activity);

//        publish to rabbit mq for AI activities
try {
    rabbitTemplate.convertAndSend(exchangeName,routingKey,savedActivity);
} catch (Exception e) {
log.error("failed to publish activity to RabbitMq: ",e);
}

        return mapToResponse(savedActivity);

    }


    private ActivityResponse mapToResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .additionalMetrics(activity.getAdditionalMetrics())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> userActivityList = activityRepo.findByUserId(userId);
        return userActivityList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {
        Optional<Activity> activityById = activityRepo.findById(activityId);
        Activity activity = activityById.orElseThrow(() -> new RuntimeException("no Activity found by this Id" + activityId));
        return mapToResponse(activity);
    }
}
