package org.team_alilm.common.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
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
class SecurityConfig (
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
    private val customFailureHandler: CustomFailureHandler
) {

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 무시
                .requestMatchers("/actuator/**")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/api-docs/**")
                .requestMatchers("/favicon.ico")
                .requestMatchers("/h2-console/**")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("health/**")
                .requestMatchers("/actuator/health")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            // ✅ CORS를 Security 체인에서 활성화 (기존 disable 제거)
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOptionsCustomizer -> frameOptionsCustomizer.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.GET, *PublicApiPaths.Companion.all().toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(
                    jwtUtil = jwtUtil,
                    userDetailsService = userDetailsService,
                ),
                UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { oauth2LoginCustomizer ->
                oauth2LoginCustomizer
                    .userInfoEndpoint { userInfoEndpointCustomizer ->
                        userInfoEndpointCustomizer.userService(customOAuth2UserService)
                    }
                    .successHandler(customSuccessHandler)
                    .failureHandler(customFailureHandler)
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration()
        cfg.setAllowedOrigins(
            mutableListOf<String?>(
                "http://localhost:3000", "http://127.0.0.1:3000", "https://algamja.com"
            )
        )
        cfg.setAllowedMethods(mutableListOf<String?>("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"))
        cfg.setAllowedHeaders(mutableListOf<String?>("Authorization", "Content-Type", "Accept", "X-Requested-With"))
        cfg.setAllowCredentials(true)
        cfg.setMaxAge(3600L)
        val src = UrlBasedCorsConfigurationSource()
        src.registerCorsConfiguration("/**", cfg)
        return src
    }
}