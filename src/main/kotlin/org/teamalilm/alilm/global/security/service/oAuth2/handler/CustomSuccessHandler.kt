package org.teamalilm.alilm.global.security.service.oAuth2.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import org.teamalilm.alilm.application.port.out.AddMemberPort
import org.teamalilm.alilm.application.port.out.AddMemberRoleMappingPort
import org.teamalilm.alilm.application.port.out.LoadMemberPort
import org.teamalilm.alilm.application.port.out.LoadRolePort
import org.teamalilm.alilm.common.error.ErrorMessage
import org.teamalilm.alilm.common.error.NotFoundRoleException
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Role
import org.teamalilm.alilm.adapter.out.gateway.MailGateway
import org.teamalilm.alilm.adapter.out.gateway.SlackGateway
import org.teamalilm.alilm.global.security.jwt.JwtUtil
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

@Component
@Transactional(readOnly = true)
class CustomSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val loadMemberPort: LoadMemberPort,
    private val addMemberPort: AddMemberPort,
    private val addMemberRoleMappingPort: AddMemberRoleMappingPort,
    private val loadRolePort: LoadRolePort,
    private val slackGateway: SlackGateway,
    private val mailGateway: MailGateway,
    @Value("\${app.base-url}") private val baseUrl: String // baseUrl을 동적으로 주입
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
            log.info("attributes: $attributes")

            val providerId = attributes["id"]?.toString()
                ?: throw IllegalStateException("OAuth2 응답에 이메일이 없습니다.")

            val provider = attributes["provider"]?.toString()?.let { Provider.from(it) }
                ?: throw IllegalStateException("OAuth2 응답에 공급자가 없습니다.")

            val member = when (val member = loadMemberPort.loadMember(provider, providerId)) {
                null -> saveMember(attributes)
                    .also { saveMemberRoleMapping(it) }
                else -> updateMember(attributes, member)
            }
            log.info("member: $member")

            val memberId = member.id
            val jwt = jwtUtil.createJwt(memberId!!, 1000L * 60 * 60 * 24 * 30)
            log.info("jwt: $jwt")

            val redirectUri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/oauth/kakao")
                .queryParam("Authorization", jwt)
                .build()
                .toUriString()

            try{
                redirectStrategy.sendRedirect(request, response, redirectUri)
            }catch (e: Exception){
                log.error("error: $e")
            }
        }

    }

    private fun saveMember(attributes: Map<String, Any>): Member {
        val provider = attributes["provider"] as? String
            ?: throw IllegalStateException("OAuth2 응답에 공급자가 없습니다.")
        log.info("provider: $provider")
        val providerId = attributes["id"] as? Long ?: throw IllegalStateException("")
        log.info("providerId: $providerId")
        val email = attributes["email"] as? String ?: throw IllegalStateException("")
        val nickname = attributes["nickname"] as? String ?: throw IllegalStateException("")

        slackGateway.sendMessage("새로운 회원이 가입했습니다. \nemail: $email \nnickname: $nickname")
        mailGateway.sendMail("알림 회원가입을 환영합니다. 😊", email)

        return addMemberPort.addMember(
            Member(
                id = null,
                provider = Provider.from(provider),
                providerId = providerId,
                email = email,
                nickname = nickname,
            )
        )
    }

    private fun saveMemberRoleMapping(member: Member) {
        val role = loadRolePort.loadRole(Role.RoleType.ROLE_USER) ?: throw NotFoundRoleException(ErrorMessage.NOT_FOUND_ROLE)

        addMemberRoleMappingPort.addMemberRoleMapping(
            member = member,
            role = role
        )
    }

    private fun updateMember(attributes: Map<String, Any>, member: Member): Member {
        // Slack 메시지 전송
        slackGateway.sendMessage("기존 회원이 로그인했습니다. \nemail: ${member.email} \nnickname: ${member.nickname}")

        // OAuth2 응답에서 가져온 정보 추출
        val newNickname = attributes["nickname"] as? String ?: throw IllegalStateException("OAuth2 응답에 닉네임이 없습니다.")

        // 기존 회원 정보 업데이트
        member.update(newNickname)

        return member
    }
}