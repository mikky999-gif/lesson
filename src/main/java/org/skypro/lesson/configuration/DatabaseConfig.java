package org.skypro.lesson.configuration;

import org.hibernate.boot.model.TypeContributor;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;


public abstract class DatabaseConfig implements TypeContributor {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> hibernateProperties.put(
                "hibernate.type.contributor",
                "com.vladmihalcea.hibernate.type.util.ConfigurationContributorImpl"
        );
    }
}