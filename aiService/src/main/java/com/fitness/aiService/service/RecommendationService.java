package com.fitness.aiService.service;

import com.fitness.aiService.model.Recommendation;
import com.fitness.aiService.repo.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;


    public List<Recommendation> getUserRecommendations(String userId) {
        return repository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendations(String activityId) {
        return repository.findByActivityId(activityId).orElseThrow(() -> new RuntimeException("no recommendation found for this activity " + activityId));
    }
}
