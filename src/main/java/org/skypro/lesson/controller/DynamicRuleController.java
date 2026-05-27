package org.skypro.lesson.controller;

import org.skypro.lesson.dto.response.RulesResponseDTO;
import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.model.RuleStats;
import org.skypro.lesson.repository.RuleStatsRepository;
import org.skypro.lesson.repository.TransactionRepository;
import org.skypro.lesson.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rule", produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;
    private final RuleStatsRepository ruleStatsRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DynamicRuleController(
            DynamicRuleService dynamicRuleService,
            RuleStatsRepository ruleStatsRepository,
            TransactionRepository transactionRepository) {
        this.dynamicRuleService = dynamicRuleService;
        this.ruleStatsRepository = ruleStatsRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DynamicRule> addDynamicRule(@RequestBody DynamicRule rule) {
        DynamicRule savedRule = dynamicRuleService.addDynamicRule(rule);
        return ResponseEntity.ok(savedRule);
    }

    @GetMapping
    public ResponseEntity<RulesResponseDTO> listDynamicRules() {
        List<DynamicRule> rules = dynamicRuleService.listDynamicRules();
        RulesResponseDTO response = new RulesResponseDTO(rules);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDynamicRule(@PathVariable String id) {
        dynamicRuleService.deleteDynamicRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rule/stats")
    public ResponseEntity<Map<String, Object>> getRuleStats() {
        List<RuleStats> stats = ruleStatsRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("stats", stats.stream()
                .map(stat -> Map.of("rule_id", stat.getRule().getId(), "count", stat.getCounter()))
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/management/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        transactionRepository.clearCaches();
        return ResponseEntity.noContent().build();
    }

    @Autowired
    BuildProperties buildProperties;

    @GetMapping("/management/info")
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", buildProperties.getArtifact());
        response.put("version", buildProperties.getVersion());
        return ResponseEntity.ok(response);
    }
}