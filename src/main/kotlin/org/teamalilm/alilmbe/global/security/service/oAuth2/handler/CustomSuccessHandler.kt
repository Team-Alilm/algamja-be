package org.teamalilm.alilmbe.global.security.service.oAuth2.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.member.entity.Role
import org.teamalilm.alilmbe.domain.member.repository.MemberRepository
import org.teamalilm.alilmbe.global.security.jwt.JwtUtil
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.OAuth2Provider

@Component
@Transactional(readOnly = true)
class CustomSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val memberRepository: MemberRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {

        if (authentication?.principal is OAuth2User) {
            val oAuth2User = authentication.principal as OAuth2User
            val attributes = oAuth2User.attributes

            val email = attributes["email"]?.toString()
                ?: throw IllegalStateException("OAuth2 응답에 이메일이 없습니다.")
            
            val newMember = when (val member = memberRepository.findByEmail(email)) {
                null -> saveMember(attributes)
                else -> updateMember(attributes, member)
            }

            val memberId = memberRepository.save(newMember).id ?: throw IllegalStateException("")
            val baseUrl = """https://alilm.co.kr"""

            val redirectUri = UriComponentsBuilder.fromOriginHeader(baseUrl)
                .path("/oauth/kakao")
                .queryParam("token", jwtUtil.createJwt(memberId, 1000 * 60 * 60))
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
        val nickname = attributes["nickname"] as? String ?: throw IllegalStateException("")

        return Member(
            provider = OAuth2Provider.from(provider),
            providerId = providerId,
            email = email,
            nickname = nickname,
            role = Role.MEMBER
        )
    }

    private fun updateMember(attributes: Map<String, Any>, member: Member): Member {
        val email = attributes["email"] as? String ?: throw IllegalStateException("")
        member.update(email)

        return member
    }
}