package org.team_alilm.global.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class CompositeTransactionConfig {

    @Bean
    @Primary
    fun transactionManager(
        entityManagerFactory: EntityManagerFactory
    ): JpaTransactionManager = JpaTransactionManager(entityManagerFactory)
}