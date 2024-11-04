package org.team_alilm.config

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
import org.team_alilm.handler.CustomFailureHandler
import org.team_alilm.service.CustomUserDetailsService
import org.team_alilm.handler.CustomSuccessHandler
import org.team_alilm.jwt.JwtFilter
import org.team_alilm.jwt.JwtUtil
import org.team_alilm.service.CustomOAuth2UserService

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
                .requestMatchers(HttpMethod.GET, "/api/v1/baskets")
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**")
                .requestMatchers(HttpMethod.GET, "/api/v1/alilms/**")
                .requestMatchers("/health-check")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/swagger-resources/**")
                .requestMatchers("/api-docs/**")
                .requestMatchers("/favicon.ico")
                .requestMatchers("/h2-console/**")
        }
    }

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

}
