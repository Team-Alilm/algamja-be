// TxConfig.kt
package org.team_alilm.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class TxConfig {
    @Bean(name = ["transactionManager"])
    fun exposedTxManager(ds: DataSource) =
        org.jetbrains.exposed.spring.SpringTransactionManager(ds)
}