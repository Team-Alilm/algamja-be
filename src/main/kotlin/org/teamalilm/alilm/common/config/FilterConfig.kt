package org.teamalilm.alilm.common.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.teamalilm.alilm.common.filter.LoggingFilter

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