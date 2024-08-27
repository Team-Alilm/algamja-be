package org.teamalilm.alilm.global.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        // 리다이렉트 없이 401 Unauthorized 응답을 보냄
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
    }
}
