package org.skypro.lesson.dto;

import org.skypro.lesson.model.Recommendation;

import java.util.UUID;

public interface Ruleset {
    Recommendation check(UUID userId);
}
