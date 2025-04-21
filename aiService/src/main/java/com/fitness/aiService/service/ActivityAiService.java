package com.fitness.aiService.service;

import com.fitness.aiService.model.Activity;
import com.fitness.aiService.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt=creatPromptFromActivity(activity);
        String aiResponse=geminiService.getAnswer(prompt);
        log.info("response from Ai: {}",aiResponse);
    }

    private String creatPromptFromActivity(Activity activity) {

    }
}
