package org.skypro.lesson.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypro.lesson.controller.DynamicRuleController;
import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.model.DynamicRule.QueryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {DynamicRuleController.class})
@ActiveProfiles("test")
public class IntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddAndRetrieveDynamicRule() throws Exception {

        DynamicRule rule = new DynamicRule();
        rule.setProductName("Инвестируй выгодно!");
        rule.setProductId(UUID.randomUUID());
        rule.setProductText("Рекомендованный продукт для инвестиций.");

        QueryDefinition userOfCondition = new QueryDefinition();
        userOfCondition.setQuery("USER_OF");
        userOfCondition.setArguments(Collections.singletonList("CREDIT"));
        userOfCondition.setNegate(true);

        QueryDefinition transactionSumCondition = new QueryDefinition();
        transactionSumCondition.setQuery("TRANSACTION_SUM_COMPARE");
        transactionSumCondition.setArguments(java.util.Arrays.asList("DEBIT", ">", "100000"));
        transactionSumCondition.setNegate(false);

        rule.setRuleDefinition(java.util.Arrays.asList(userOfCondition, transactionSumCondition));

        HttpHeaders headers = new HttpHeaders();
        URI uri = new URI("http://localhost:" + port + "/rule");
        HttpEntity<DynamicRule> request = new HttpEntity<>(rule, headers);
        ResponseEntity<DynamicRule> response = restTemplate.exchange(uri, HttpMethod.POST, request, DynamicRule.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].productName").value(rule.getProductName()))
                .andExpect(jsonPath("$.[0].productId").value(rule.getProductId().toString()));
    }
}