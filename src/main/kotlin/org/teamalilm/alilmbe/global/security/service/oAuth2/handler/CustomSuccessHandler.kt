package org.teamalilm.alilmbe.global.security.service.oAuth2.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Role
import org.teamalilm.alilmbe.domain.member.repository.MemberRepository
import org.teamalilm.alilmbe.global.security.jwt.JwtUtil
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.Provider
import org.teamalilm.alilmbe.global.slack.service.SlackService

private const val BASE_URL = """https://alilm.co.kr"""

@Component
@Transactional(readOnly = true)
class CustomSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val memberRepository: MemberRepository,
    private val slackService: SlackService
) : SimpleUrlAuthenticationSuccessHandler() {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {

        if (authentication?.principal is OAuth2User) {
            val oAuth2User = authentication.principal as OAuth2User
            val attributes = oAuth2User.attributes

            val email = attributes["phoneNumber"]?.toString()
                ?: throw IllegalStateException("OAuth2 응답에 이메일이 없습니다.")

            val newMember = when (val member = memberRepository.findByPhoneNumber(email)) {
                null -> saveMember(attributes)
                else -> updateMember(attributes, member)
            }

            val memberId = memberRepository.save(newMember).id ?: throw IllegalStateException("")
            val jwt = jwtUtil.createJwt(memberId, 1000 * 60 * 60)
            log.info("jwt: $jwt")

            val redirectUri = UriComponentsBuilder.fromOriginHeader(BASE_URL)
                .path("/oauth/kakao")
                .queryParam("Authorization", jwtUtil.createJwt(memberId, 1000 * 60 * 60))
                .build()
                .toUriString()

            redirectStrategy.sendRedirect(request, response, redirectUri)
        }

    }

    private fun saveMember(attributes: Map<String, Any>): Member {
        val provider = attributes["provider"] as? String
            ?: throw IllegalStateException("OAuth2 응답에 공급자가 없습니다.")
        val providerId = attributes["id"] as? Long ?: throw IllegalStateException("")
        val email = attributes["email"] as? String ?: throw IllegalStateException("")
        val phoneNumber = attributes["phoneNumber"] as? String ?: throw IllegalStateException("")
        val nickname = attributes["nickname"] as? String ?: throw IllegalStateException("")

        slackService.sendSlackMessage("새로운 회원이 가입했습니다. \nemail: $email \nphoneNumber: $phoneNumber \nnickname: $nickname")

        return Member(
            provider = Provider.from(provider),
            providerId = providerId,
            email = email,
            phoneNumber = phoneNumber,
            nickname = nickname,
            role = Role.MEMBER
        )
    }

    private fun updateMember(attributes: Map<String, Any>, member: Member): Member {
        slackService.sendSlackMessage("기존 회원이 로그인했습니다. \nemail: ${member.email} \nphoneNumber: ${member.phoneNumber} \nnickname: ${member.nickname}")

        val phoneNumber = attributes["phoneNumber"] as? String ?: throw IllegalStateException("")
        member.updatePhoneNumber(phoneNumber)

        return member
    }
}