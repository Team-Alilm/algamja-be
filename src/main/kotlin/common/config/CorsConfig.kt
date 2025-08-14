package org.team_alilm.common.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.team_alilm.common.props.CorsProps

@Configuration
@EnableConfigurationProperties(CorsProps::class)
class CorsConfig(
    private val props: CorsProps
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            // 와일드카드/패턴 허용하려면 allowedOriginPatterns 사용
            .allowedOriginPatterns(*props.origins.toTypedArray())
            .allowedMethods(*props.methods.toTypedArray())
            .allowedHeaders(*props.headers.toTypedArray())
            .allowCredentials(props.allowCredentials)
            .maxAge(props.maxAge)
    }
}