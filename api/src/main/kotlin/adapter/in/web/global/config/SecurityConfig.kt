package org.team_alilm.adapter.`in`.web.global.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.team_alilm.application.service.security.CustomUserDetailsService
import org.team_alilm.adapter.`in`.web.global.security.jwt.JwtFilter
import org.team_alilm.adapter.`in`.web.global.security.jwt.JwtUtil
import org.team_alilm.adapter.`in`.web.global.security.oAuth2.handler.CustomSuccessHandler
import org.team_alilm.adapter.`in`.web.global.security.oAuth2.service.CustomOAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) {

    @Bean
    fun excludedPaths(): List<String> {
        return listOf(
            "/api/v1/baskets",
            "/api/v1/products/price",
            "/api/v1/notifications/count",
            "/health-check",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/api-docs/**",
            "/favicon.ico",
            "/login/**",
            "/h2-console/**",
        )
    }

    @Bean
    fun jwtFilter(): JwtFilter {
        return JwtFilter(jwtUtil, userDetailsService, excludedPaths())
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 무시
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOptionsCustomizer -> frameOptionsCustomizer.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorizeRequest ->
                authorizeRequest
                    .requestMatchers(*excludedPaths().toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { oauth2LoginCustomizer ->
                oauth2LoginCustomizer
                    .userInfoEndpoint { userInfoEndpointCustomizer ->
                        userInfoEndpointCustomizer.userService(customOAuth2UserService)
                    }
                    .successHandler(customSuccessHandler)
            }

        return http.build()
    }

}
