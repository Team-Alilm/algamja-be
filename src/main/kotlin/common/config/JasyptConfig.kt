package org.team_alilm.global.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig(
    @Value("\${jasypt.encryptor.password}")
    private val password: String,

    @Value("\${jasypt.encryptor.algorithm}")
    private val algorithm: String
) {

    @Bean(name = ["jasyptStringEncryptor"])
    fun stringEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()

        config.password = this.password
        config.algorithm = this.algorithm

        config.poolSize = 1
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        config.stringOutputType = "base64"
        encryptor.setConfig(config)

        return encryptor
    }
    
}