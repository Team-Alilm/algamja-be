package org.team_alilm.common.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

// TxAliasConfig.kt  (폴백: exposedTxManager가 없으면 기본 TM을 alias로 노출)
@Configuration
class TxAliasConfig {
    @Bean(name = ["exposedTxManager"])
    @ConditionalOnMissingBean(name = ["exposedTxManager"])
    fun exposedTxAlias(platformTxManager: PlatformTransactionManager): PlatformTransactionManager =
        platformTxManager
}