package org.team_alilm.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
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
            .addSecurityItem(securityRequirement)
            .components(components)
    }

    @Bean
    fun userApi() : GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("User API")
            .pathsToMatch("/api/*/user/**")
            .build()
    }

    companion object {
        private const val BEARER_TOKEN_PREFIX = "bearer"
    }
}