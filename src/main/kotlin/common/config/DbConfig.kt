// DbConfig.kt
package org.team_alilm.common.config

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DbConfig {

    @Bean
    @ConditionalOnBean(DataSource::class)
    fun database(ds: DataSource): Database = Database.connect(ds)

    @Bean(name = ["exposedTxManager"])
    @ConditionalOnBean(DataSource::class)
    fun exposedTxManager(ds: DataSource) = SpringTransactionManager(ds)
}