package org.team_alilm.common.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.team_alilm.common.security.jwt.JwtFilter
import org.team_alilm.common.security.jwt.JwtUtil
import org.team_alilm.common.security.oauth.CustomOAuth2UserService

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val customFailureHandler: CustomFailureHandler,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .headers { it.frameOptions { fo -> fo.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    // 정적/문서/헬스 오픈
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/favicon.ico",
                        "/h2-console/**"
                    ).permitAll()

                    // CORS Preflight 안전판
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // 🔓 Public GET API (enum 기반, 버전 프리픽스 `/api/v*/...`)
                    .requestMatchers(HttpMethod.GET, *PublicApiPaths.all().toTypedArray()).permitAll()

                    // 나머지는 인증
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(jwtUtil = jwtUtil, userDetailsService = userDetailsService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .oauth2Login {
                it.userInfoEndpoint { u -> u.userService(customOAuth2UserService) }
                    .successHandler(customSuccessHandler)
                    .failureHandler(customFailureHandler)
            }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val c = CorsConfiguration().apply {
            // 🔸 패턴 대신 정확한 오리진 나열
            allowedOrigins = listOf(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "https://algamja.com",
                "https://api.algamja.com"
            )
            allowedMethods = listOf("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization","Content-Disposition","X-Total-Count")
            allowCredentials = true
            maxAge = 3600
        }
        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", c) }
    }
}