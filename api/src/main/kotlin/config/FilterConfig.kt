package org.team_alilm.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.team_alilm.config.filter.LoggingFilter

@Configuration
class FilterConfig {

    @Bean
    fun loggingFilter(): FilterRegistrationBean<LoggingFilter> {
        val registrationBean = FilterRegistrationBean<LoggingFilter>()
        registrationBean.filter = LoggingFilter()
        registrationBean.order = 1 // Set filter order if needed
        return registrationBean
    }

}