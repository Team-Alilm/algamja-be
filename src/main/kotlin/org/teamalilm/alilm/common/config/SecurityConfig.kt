package org.teamalilm.alilm.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector
import org.teamalilm.alilm.global.security.ExcludedUrls
import org.teamalilm.alilm.global.security.jwt.JwtFilter
import org.teamalilm.alilm.global.security.service.oAuth2.handler.CustomSuccessHandler
import org.teamalilm.alilm.global.security.service.oAuth2.service.CustomOAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        introspector: HandlerMappingIntrospector
    ): SecurityFilterChain {
        http
            .cors { it.disable() }

            .formLogin {
                it.disable()
            }

            .httpBasic {
                it.disable()
            }

            .csrf { it.disable() }

            .headers { it.frameOptions { frameOptionsCustomizer -> frameOptionsCustomizer.sameOrigin() } }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { authorizeRequest ->
                ExcludedUrls.entries.forEach { excludedUrl ->
                    val method = excludedUrl.methode

                    if (method == null) {
                        authorizeRequest.requestMatchers(excludedUrl.path).permitAll()
                    } else {
                        authorizeRequest.requestMatchers(method, excludedUrl.path).permitAll()
                    }
                }

                authorizeRequest.anyRequest().authenticated()
            }

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

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