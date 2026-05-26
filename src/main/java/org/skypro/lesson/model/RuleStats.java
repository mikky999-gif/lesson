package org.skypro.lesson.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "rule_stats")
@Data
public class RuleStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private DynamicRule rule;

    @Column(name = "counter")
    private long counter;
}