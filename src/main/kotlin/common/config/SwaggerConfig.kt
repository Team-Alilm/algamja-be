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
        title = "Algamja API Docs",
        description = "Algamja의 API 명세서를 알려드려요.",
        version = "v1"
    )
)
@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(securityRequirement())
            .components(securityComponents())
    }

    @Bean
    fun v1Api(): GroupedOpenApi = buildApiGroup("v1", "/api/v1/**")

    @Bean
    fun v2Api(): GroupedOpenApi = buildApiGroup("v2", "/api/v2/**")

    @Bean
    fun v3Api(): GroupedOpenApi = buildApiGroup("v3", "/api/v3/**")

    private fun buildApiGroup(group: String, path: String): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group(group.uppercase() + " API")
            .pathsToMatch(path)
            .build()
    }

    private fun securityRequirement(): SecurityRequirement {
        return SecurityRequirement().addList(JWT_HEADER_NAME)
    }

    private fun securityComponents(): Components {
        return Components().addSecuritySchemes(
            JWT_HEADER_NAME,
            SecurityScheme()
                .name(JWT_HEADER_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat("JWT")
        )
    }

    companion object {
        private const val JWT_HEADER_NAME = "Authorization"
        private const val BEARER = "bearer"
    }
}