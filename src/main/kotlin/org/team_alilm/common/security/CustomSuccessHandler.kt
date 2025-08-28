package org.team_alilm.common.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import org.team_alilm.common.enums.Provider
import org.team_alilm.common.security.jwt.JwtUtil
import org.team_alilm.common.security.oauth.OauthLoginMemberService
import org.team_alilm.common.slack.SlackClient

@Component
@Transactional
class CustomSuccessHandler(
    private val oauthLoginMemberService: OauthLoginMemberService,  // Member 관련 서비스 계층
    private val jwtUtil: JwtUtil,
    private val slackClient: SlackClient,
    @Value("\${app.base-url}") private val baseUrl: String
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val oAuth2User = authentication?.principal as OAuth2User
        val attributes = oAuth2User.attributes
        val providerId = attributes["id"].toString()
        val provider = Provider.from(attributes["provider"].toString())

        val memberId = oauthLoginMemberService.loginMember(provider, providerId, attributes)  // 비즈니스 로직 처리
        attributes["nickname"].toString()
        // Slack 메시지 전송
        slackClient.sendMessage("""
            로그인 성공 : ${attributes["nickname"].toString()}
            이메일 : ${attributes["email"].toString()}
            """.trimIndent())

        val jwt = jwtUtil.createJwt(memberId, 1000L * 60 * 60 * 24 * 30 * 1000)
        val redirectUri = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/oauth/kakao")
            .queryParam("Authorization", jwt)
            .build()
            .toUriString()

        redirectStrategy.sendRedirect(request, response, redirectUri)
    }
}
