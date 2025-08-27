package org.team_alilm.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    @Value("\${api.server-url:/}") private val serverUrl: String  // ← 환경별 주입
) {

    @Bean
    fun openAPI(): OpenAPI {
        val scheme = "bearerAuth"
        return OpenAPI()
            .info(Info().title("Alim API").description("알감자 백엔드 API 문서").version("v1.0"))
            .components(
                Components().addSecuritySchemes(
                    scheme,
                    SecurityScheme().type(SecurityScheme.Type.HTTP)
                        .scheme("bearer").bearerFormat("JWT").name("Authorization")
                )
            )
            .addSecurityItem(SecurityRequirement().addList(scheme))
            .servers(listOf(Server().url(serverUrl)))  // ← 환경별 서버 반영
    }

    @Bean
    fun apiGroup(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("api")
            .pathsToMatch("/api/**")
            .build()

    @Bean
    fun swaggerUiParametersRunner(
        provider: ObjectProvider<SwaggerUiConfigParameters>
    ) = ApplicationRunner {
        provider.ifAvailable { params ->
            params.setUrl("/v3/api-docs/api")  // Swagger UI 기본 로딩 스펙
        }
    }
}