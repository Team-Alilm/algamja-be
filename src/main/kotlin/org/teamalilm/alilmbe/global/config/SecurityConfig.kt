package org.teamalilm.alilmbe.global.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.teamalilm.alilmbe.global.handler.CustomSuccessHandler
import org.teamalilm.alilmbe.global.jwt.JwtFilter
import org.teamalilm.alilmbe.global.service.CustomOAuth2UserService

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
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.disable() }

            .formLogin {
                it.disable()
            }

            .httpBasic {
                it.disable()
            }

            .csrf {
                it.disable()
            }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .oauth2Login { oauth2LoginCustomizer ->
                oauth2LoginCustomizer
                    .userInfoEndpoint { userInfoEndpointCustomizer ->
                        userInfoEndpointCustomizer.userService(customOAuth2UserService)
                    }

                    .successHandler(customSuccessHandler)
            }

            .authorizeHttpRequests { auth ->
                auth
                    .anyRequest().authenticated()
            }

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}