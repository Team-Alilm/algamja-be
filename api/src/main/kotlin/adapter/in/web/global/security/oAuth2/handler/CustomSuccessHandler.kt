package org.team_alilm.adapter.`in`.web.global.security.oAuth2.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.team_alilm.adapter.`in`.web.global.security.jwt.JwtUtil
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.service.OauthLoginMemberService
import org.team_alilm.domain.Member

@Component
class CustomSuccessHandler(
    private val oauthLoginMemberService: OauthLoginMemberService,  // Member 관련 서비스 계층
    private val jwtUtil: JwtUtil,
    private val slackGateway: SlackGateway,
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
        val provider = Member.Provider.from(attributes["provider"].toString())

        val member = oauthLoginMemberService.loginMember(provider, providerId, attributes)  // 비즈니스 로직 처리

        // Slack 메시지 전송
        slackGateway.sendMessage("""
            로그인 성공 : ${member.nickname}
            이메일 : ${member.email}
            """.trimIndent())

        val jwt = jwtUtil.createJwt(member.id!!, 1000L * 60 * 60 * 24 * 30)
        val redirectUri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/oauth/kakao")
            .queryParam("Authorization", jwt)
            .build().toUriString()

        redirectStrategy.sendRedirect(request, response, redirectUri)
    }
}
