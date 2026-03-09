package org.skypro.lesson.controller;

import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.service.RecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationsService recommendationsService;

    @Autowired
    public RecommendationController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = recommendationsService.getRecommendations(userId);

        return ResponseEntity.ok(Map.of(
                "user_id", userId.toString(),
                "recommendations", recommendations
        ));
    }
}