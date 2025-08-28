package org.team_alilm.algamja.common.config

import java.util.Properties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig(
    @Value($$"${spring.mail.username}")
    private val username: String,
    @Value($$"${spring.mail.password}")
    private val password: String,
    @Value($$"${spring.mail.host}")
    private val host: String,
    @Value($$"${spring.mail.port}")
    private val port: Int
) {

    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = this.host
        mailSender.port = this.port
        mailSender.username = this.username
        mailSender.password = this.password

        mailSender.javaMailProperties = getMailProperties()

        return mailSender
    }

    private fun getMailProperties(): Properties {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        return props
    }
}