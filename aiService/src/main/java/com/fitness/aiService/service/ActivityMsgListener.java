package com.fitness.aiService.service;

import com.fitness.aiService.model.Activity;
import com.fitness.aiService.model.Recommendation;
import com.fitness.aiService.repo.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMsgListener {

    private final ActivityAiService aiService;
    private final RecommendationRepository recommendationRepository;


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity) {
        log.info("Received activity for  Processing : {}", activity);
        Recommendation recommendation = aiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
        log.info("recommendation saved successfully with id: {}", recommendation.getId());
    }

}
