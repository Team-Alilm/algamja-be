package org.teamalilm.alilm.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Alilm API Docs",
        description = "Alilm의 API 명세서를 알려드려요.",
        version = "v1"
    )
)
@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val jwtHeaderName = "Authorization"

        val securityRequirement = SecurityRequirement().addList(jwtHeaderName)

        val components = Components()
            .addSecuritySchemes(
                jwtHeaderName, SecurityScheme()
                    .name(jwtHeaderName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme(BEARER_TOKEN_PREFIX)
                    .bearerFormat(jwtHeaderName)
            )

        return OpenAPI()
            .servers(
                listOf(
                    Server().url("https://alilm.store").description("prod Server"),
                    Server().url("http://localhost:8080").description("dev Server")
                )
            )
            .addSecurityItem(securityRequirement)
            .components(components)
    }

    companion object {
        private const val BEARER_TOKEN_PREFIX = "Bearer"
    }

}