package org.team_alilm.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.util.UriComponentsBuilder

class CustomFailureHandler(
    @Value("\${app.base-url}") private val baseUrl: String
) : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.characterEncoding = "UTF-8"
        response?.writer?.write(
            """
            {
                "message": "로그인에 실패하였습니다."
            }
            """.trimIndent()
        )

        val redirectUri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/oauth/kakao")
            .build().toUriString()

        redirectStrategy.sendRedirect(request, response, redirectUri)
    }
}