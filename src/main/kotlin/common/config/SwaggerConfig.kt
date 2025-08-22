package org.team_alilm.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val scheme = "bearerAuth"
        return OpenAPI()
            .info(Info().title("Alim API").description("알감자 백엔드 API 문서").version("v1.0"))
            .components(
                Components().addSecuritySchemes(
                    scheme,
                    SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").name("Authorization")
                )
            )
            .addSecurityItem(SecurityRequirement().addList(scheme))
    }

    @Bean
    fun apiGroup(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("api")
            .pathsToMatch("/api/**")   // -> /v3/api-docs/api 가 생성됨
            .build()                    //  [oai_citation:5‡OpenAPI 3 Library for spring-boot](https://springdoc.org/faq.html?utm_source=chatgpt.com)

    @Bean
    fun swaggerUiParametersRunner(
        provider: ObjectProvider<SwaggerUiConfigParameters>
    ) = ApplicationRunner {
        provider.ifAvailable { params ->
            params.setUrl("/v3/api-docs/api")  // 기본 로딩 스펙 지정
        }
    }
}