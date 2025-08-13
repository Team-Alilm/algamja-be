package org.team_alilm.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig(

    @Value("\${cors.origins}")
    private val origins: List<String>,

    @Value("\${cors.methods}")
    private val methods: List<String>,

    @Value("\${cors.headers}")
    private val headers: List<String>,

    @Value("\${cors.allow-credentials:false}")
    private val allowCredentials: Boolean,

    @Value("\${cors.max-age:3600}")
    private val maxAge: Long
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration().apply {
            if (allowCredentials) {
                // credentials=true에서는 wildcard(*) 금지 → patterns 사용
                allowedOriginPatterns = origins
            } else {
                allowedOrigins = origins
            }
            allowedMethods = methods
            allowedHeaders = headers
            this.allowCredentials = allowCredentials
            this.maxAge = maxAge
        }
        return UrlBasedCorsConfigurationSource().also {
            it.registerCorsConfiguration("/**", cfg)
        }
    }
}