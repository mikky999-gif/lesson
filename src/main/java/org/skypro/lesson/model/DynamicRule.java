package org.skypro.lesson.model;

import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Table("dynamic_rule")
@Data
public class DynamicRule {

    @Id
    @Column("id")
    private UUID id;

    @Column("product_name")
    private String productName;

    @Column("product_id")
    private UUID productId;

    @Column("product_text")
    private String productText;

    @Column("rule_definition")
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