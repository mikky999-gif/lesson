package org.skypro.lesson.repository.jpa;

import org.skypro.lesson.model.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DynamicRuleJpaRepository extends JpaRepository<DynamicRule, UUID> {
}