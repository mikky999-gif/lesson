package org.skypro.lesson.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.skypro.lesson.converter.QueryDefinitionConverter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rule")
@Data
public class DynamicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_text")
    private String productText;

    @Column(name = "rule_definition")
    @Convert(converter = QueryDefinitionConverter.class)
    @JsonProperty("rule")
    private List<QueryDefinition> ruleDefinition;

    @Data
    public static class QueryDefinition {
        private String query;
        private List<Object> arguments;
        private Boolean negate;

        public Boolean isNegate() {
            return negate;
        }
    }
}