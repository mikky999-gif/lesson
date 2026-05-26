package org.skypro.lesson.controller;

import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController@RequestMapping(value = "/rule", produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    @Autowired
    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DynamicRule> addDynamicRule(@RequestBody DynamicRule rule) {
        DynamicRule savedRule = dynamicRuleService.addDynamicRule(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRule);
    }

    @GetMapping
    public ResponseEntity<List<DynamicRule>> listDynamicRules() {
        List<DynamicRule> rules = dynamicRuleService.listDynamicRules();
        return ResponseEntity.ok(rules);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDynamicRule(@PathVariable String id) {
        dynamicRuleService.deleteDynamicRule(id);
        return ResponseEntity.noContent().build();
    }

}