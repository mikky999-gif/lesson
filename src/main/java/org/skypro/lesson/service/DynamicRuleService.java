package org.skypro.lesson.service;

import com.vladmihalcea.spring.repository.BaseJpaRepository;
import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.repository.RuleStatsRepository;
import org.skypro.lesson.repository.jpa.DynamicRuleJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleJpaRepository dynamicRuleRepository;
    private final RuleStatsRepository ruleStatsRepository;
    private BaseJpaRepository dynamicRuleJpaRepository;

    @Autowired
    public DynamicRuleService(
            DynamicRuleJpaRepository dynamicRuleRepository,
            RuleStatsRepository ruleStatsRepository) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    public DynamicRule addDynamicRule(DynamicRule rule) {
        return dynamicRuleRepository.save(rule);
    }

    public List<DynamicRule> listDynamicRules() {
        return dynamicRuleRepository.findAll();
    }

    public void deleteDynamicRule(String id) {
        UUID ruleId = UUID.fromString(id);
        dynamicRuleJpaRepository.deleteById(ruleId);
        ruleStatsRepository.deleteById(ruleId);
    }
}