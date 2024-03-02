package org.teamalilm.alilmbe.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.teamalilm.alilmbe.global.authority.JwtAuthenticationFilter
import org.teamalilm.alilmbe.global.authority.JwtProvider
import org.teamalilm.alilmbe.global.service.CustomOAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtProvider: JwtProvider,
    private val customOAuth2UserService: CustomOAuth2UserService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
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

            .oauth2Login{ oauth2LoginCustomizer ->
                oauth2LoginCustomizer.userInfoEndpoint { userInfoEndpointCustomizer ->
                    userInfoEndpointCustomizer.userService(customOAuth2UserService)
                }
            }

            .authorizeHttpRequests { auth -> auth
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            }

            .addFilterBefore(
                JwtAuthenticationFilter(jwtProvider = jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

}