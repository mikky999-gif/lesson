package org.skypro.lesson.repository;

import org.skypro.lesson.model.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, UUID> {
}