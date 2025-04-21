package com.fitness.aiService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiService.model.Activity;
import com.fitness.aiService.model.Recommendation;
import com.fitness.aiService.repo.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;
    private final ObjectMapper mapper;
    public Recommendation generateRecommendation(Activity activity) {
        String prompt = creatPromptFromActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            // Step 1: Parse AI response to extract raw JSON content
            String extractedJson = extractJsonFromAiResponse(aiResponse);
//            log.info("Extracted JSON from AI response: {}", extractedJson);

            // Step 2: Parse the recommendation structure
            JsonNode recommendationNode = mapper.readTree(extractedJson);

            // Step 3: Build analysis summary
            String formattedAnalysis = formatAnalysisSection(recommendationNode.path("analyze"));

            // Step 4: Parse improvements and suggestions
            List<String> improvements = new ArrayList<>();
            List<String> suggestions = new ArrayList<>();
            for (JsonNode node : recommendationNode.withArray("improvements")) {
                improvements.add(node.path("recommendation").asText());
                suggestions.add(node.path("area").asText());
            }

            // Step 5: Parse safety guidelines
            List<String> safety = new ArrayList<>();
            for (JsonNode node : recommendationNode.withArray("safety")) {
                safety.add(node.asText());
            }

            // Step 6: Construct and return Recommendation object
            return Recommendation.builder()
                    .userId(activity.getUserId())
                    .activityId(activity.getId())
                    .activityType(String.valueOf(activity.getType()))
                    .recommendation(formattedAnalysis)
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to process AI response for activityId={}. Error: {}", activity.getId(), e.getMessage(), e);
            return buildDefaultRecommendation(activity);
        }
    }


    private String creatPromptFromActivity(Activity activity) {
        return String.format("""
                        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                        {
                          "analyze": {
                            "overall": "Overall analysis here",
                            "pace": "Pace analysis here",
                            "heartRate": "Heart rate analysis here",
                            "caloriesBurned": "Calories analysis here"
                          },
                          "improvements": [
                            {
                              "area": "Area Name",
                              "recommendation": "Detailed recommendation"
                            }
                          ],
                          "safety": [
                            "Safety point 1",
                            "Safety point 2"
                          ]
                        }
                        
                        Analyze this activity:
                        Activity Type: %s
                        Duration: %d minutes
                        Calories Burned: %d
                        Additional Metrics: %s
                        
                        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety measures.
                        Ensure the response strictly follows the JSON format shown above.
                        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }


    private String extractJsonFromAiResponse(String aiResponse) throws JsonProcessingException {
        JsonNode rootNode = mapper.readTree(aiResponse);
        JsonNode textNode = rootNode.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");

        return textNode.asText()
                .replaceAll("\"```json\\n", "")
                .replaceAll("\\n```", "")
                .replaceAll("```json", "")
                .trim();
    }

    private String formatAnalysisSection(JsonNode analyzeNode) {
        StringBuilder sb = new StringBuilder();

        if (analyzeNode.has("overall")) {
            sb.append("🏁 Overall Analysis: ")
                    .append(analyzeNode.get("overall").asText())
                    .append("\n\n");
        }
        if (analyzeNode.has("pace")) {
            sb.append("🚶 Pace: ")
                    .append(analyzeNode.get("pace").asText())
                    .append("\n\n");
        }
        if (analyzeNode.has("heartRate")) {
            sb.append("❤️ Heart Rate: ")
                    .append(analyzeNode.get("heartRate").asText())
                    .append("\n\n");
        }
        if (analyzeNode.has("caloriesBurned")) {
            sb.append("🔥 Calories Burned: ")
                    .append(analyzeNode.get("caloriesBurned").asText())
                    .append("\n\n");
        }

        return sb.toString().trim();
    }

    private Recommendation buildDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .userId(activity.getUserId())
                .activityId(activity.getId())
                .activityType("UNKNOWN")
                .recommendation("⚠️ Default recommendation: Unable to process the activity insights at this time.")
                .improvements(List.of("No improvements available."))
                .suggestions(List.of("Review your activity or try again later."))
                .safety(List.of("Ensure safety and consult a trainer if needed."))
                .createdAt(LocalDateTime.now())
                .build();
    }

}
