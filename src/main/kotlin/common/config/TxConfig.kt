// TxConfig.kt
package org.team_alilm.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import jakarta.persistence.EntityManagerFactory

@Configuration
class TxConfig {

    /** 기본 이름 'transactionManager' 로 등록 (스프링이 기본으로 찾는 이름) */
    @Bean(name = ["transactionManager"])
    @Primary
    fun jpaTransactionManager(emf: EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager(emf)
}