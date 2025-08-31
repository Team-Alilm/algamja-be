package org.team_alilm.algamja.common.security

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
import org.team_alilm.algamja.common.security.jwt.JwtFilter
import org.team_alilm.algamja.common.security.jwt.JwtUtil
import org.team_alilm.algamja.common.security.oauth.CustomOAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val customFailureHandler: CustomFailureHandler,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
    private val env: org.springframework.core.env.Environment
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val isLocal = env.activeProfiles.any { it.equals("local", true) || it.equals("dev", true) }

        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf {
                it.disable()
                // (선택) 로컬에서만 H2 경로 CSRF 무시하고 싶다면:
                // if (isLocal) it.ignoringRequestMatchers(AntPathRequestMatcher("/h2-console/**"))
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .headers { headers ->
                headers.frameOptions { fo -> fo.sameOrigin() } // H2 콘솔용 (로컬에서만 열리게 매칭은 아래 authorize에서 제한)
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // 정적/문서 최소 공개
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/favicon.ico").permitAll()

                    // CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Actuator 최소 공개 (운영도 허용 가능)
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                    // 로컬/개발에서만 H2 콘솔 오픈
                    .apply {
                        if (isLocal) {
                            requestMatchers("/h2-console/**").permitAll()
                        } else {
                            // 운영에서는 actuator 나머지 보호 (원하면 롤 부여)
                            requestMatchers("/actuator/**").hasRole("ACTUATOR")
                        }
                    }

                    // 공개 GET API (도메인 API)
                    .requestMatchers(HttpMethod.GET, *PublicApiPaths.all().toTypedArray()).permitAll()

                    // 그 외 인증
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
            allowedOrigins = listOf(
                "http://localhost:5173", "http://127.0.0.1:5173",
                "http://localhost:3000", "http://127.0.0.1:3000",
                "https://algamja.com", "https://api.algamja.com"
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