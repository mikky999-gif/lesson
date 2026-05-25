package org.skypro.lesson.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.skypro.lesson.repository.jpa")

public class RepositoryConfig {

}