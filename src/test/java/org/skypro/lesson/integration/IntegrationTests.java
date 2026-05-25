package org.skypro.lesson.integration;

import org.junit.jupiter.api.Test;
import org.skypro.lesson.controller.DynamicRuleController;
import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.model.QueryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DynamicRuleController controller;

    @Test
    public void testAddAndRetrieveDynamicRule() throws Exception {
        DynamicRule rule = DynamicRule.builder()
                .productName("Инвестируй выгодно!")
                .productId(UUID.randomUUID())
                .productText("Рекомендованный продукт для инвестиций.")
                .ruleDefinition(java.util.Arrays.asList(
                        QueryDefinition.builder()
                                .query("USER_OF")
                                .arguments(Collections.singletonList("CREDIT"))
                                .negate(true)
                                .build(),
                        QueryDefinition.builder()
                                .query("TRANSACTION_SUM_COMPARE")
                                .arguments(java.util.Arrays.asList("DEBIT", ">", "100000"))
                                .negate(false)
                                .build()
                ))
                .build();

        controller.addDynamicRule(rule);

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(rule.getProductName())));
    }
}