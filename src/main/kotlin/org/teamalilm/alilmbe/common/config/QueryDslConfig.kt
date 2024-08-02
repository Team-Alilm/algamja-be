package org.teamalilm.alilmbe.common.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig(
    val entityManager: EntityManager
) {

    @Bean
    fun querydsl() = JPAQueryFactory(entityManager)
}