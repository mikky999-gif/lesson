package org.skypro.lesson.service;

import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.repository.jpa.DynamicRuleJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleJpaRepository dynamicRuleRepository;

    @Autowired
    public DynamicRuleService(DynamicRuleJpaRepository dynamicRuleRepository) {
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    public DynamicRule addDynamicRule(DynamicRule rule) {
        return dynamicRuleRepository.save(rule);
    }

    public List<DynamicRule> listDynamicRules() {
        return dynamicRuleRepository.findAll();
    }

    public void deleteDynamicRule(String id) {
        Optional<DynamicRule> existingRule = dynamicRuleRepository.findById(UUID.fromString(id));
        existingRule.ifPresent(dynamicRuleRepository::delete);
    }
}