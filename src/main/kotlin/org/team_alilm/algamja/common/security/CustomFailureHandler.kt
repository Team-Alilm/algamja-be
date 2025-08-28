package org.team_alilm.algamja.common.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomFailureHandler(
    @Value($$"${app.base-url}") private val baseUrl: String
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // (선택) 세션에 남은 예외 제거 – 성공 핸들러의 clearAuthenticationAttributes 역할
        val session = request.getSession(false)
        session?.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)

        // JSON 응답 or 리다이렉트 중 하나만 택1 (예시는 JSON)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8"
        response.writer.use { it.write("""{"message":"로그인에 실패하였습니다."}""") }
    }
}