package org.team_alilm.common.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
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
import org.team_alilm.common.props.CorsProps
import org.team_alilm.common.security.jwt.JwtFilter
import org.team_alilm.common.security.jwt.JwtUtil
import org.team_alilm.common.security.oauth.CustomOAuth2UserService

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CorsProps::class)
class SecurityConfig (
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
    private val customFailureHandler: CustomFailureHandler,
    private val corsProps: CorsProps,            // ← CORS 프로퍼티 주입
) {

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/api-docs/**",
                    "/favicon.ico", "/h2-console/**", "/v3/api-docs/**")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // ✅ CORS를 Security 체인에서 활성화 (기존 disable 제거)
            .cors { it.configurationSource(corsConfigurationSource()) }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .headers { it.frameOptions { f -> f.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.GET, *PublicApiPaths.all().toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(jwtUtil = jwtUtil, userDetailsService = userDetailsService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .oauth2Login { oauth ->
                oauth.userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(customSuccessHandler)
                    .failureHandler(customFailureHandler)
            }

        return http.build()
    }

    /**
     * ✅ CorsConfigurationSource를 props로부터 구성
     * - 패턴이 필요 없다면 `allowedOrigins = corsProps.origins`로 바꿔도 됨
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration().apply {
            // 패턴 사용 (예: https://*.algamja.com). 정확 매칭만 필요하면 아래 한 줄을 origins로 바꾸세요.
            this.allowedOriginPatterns = corsProps.origins

            this.allowedMethods = corsProps.methods
            this.allowedHeaders = corsProps.headers
            this.allowCredentials = corsProps.allowCredentials
            this.maxAge = corsProps.maxAge

            // allowCredentials(true) 인 경우, "*"가 props에 섞여 있지 않도록 주의.
            // (패턴/정확 도메인만 허용해야 브라우저가 쿠키 포함 요청을 허용함)
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", cfg)
        }
    }
}