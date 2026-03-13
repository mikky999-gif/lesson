package org.skypro.lesson.service;

import org.skypro.lesson.dto.Ruleset;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationsService {

    private final List<Ruleset> rulesets;

    public RecommendationsService(List<Ruleset> rulesets) {
        this.rulesets = rulesets;
    }

    public List<Recommendation> getRecommendations(UUID userId) {
        return rulesets.stream()
                .map(rule -> rule.check(userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<String, Set<User>> groupUsersByCategories(Collection<UUID> usersToCheck) {
        Map<String, Set<User>> groupedUsers = new LinkedHashMap<>();

        for (Ruleset rule : rulesets) {
            String categoryName = extractCategoryName(rule);
            groupedUsers.put(categoryName, new HashSet<>());

            for (UUID userId : usersToCheck) {
                if (Objects.nonNull(rule.check(userId))) {
                    groupedUsers.get(categoryName).add(new User(userId));
                }
            }
        }

        return groupedUsers;
    }

    private String extractCategoryName(Ruleset rule) {
        String className = rule.getClass().getSimpleName();
        if (className.endsWith("Rule")) {
            return className.substring(0, className.length() - 4);
        }
        return className;
    }
}