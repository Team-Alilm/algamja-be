package org.team_alilm.common.security.oauth

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.common.enums.Provider
import org.team_alilm.member.repository.MemberExposedRepository

@Service
@Transactional
class OauthLoginMemberService(
    private val memberExposedRepository: MemberExposedRepository
) {

    fun loginMember(provider: Provider, providerId: String, attributes: Map<String, Any>): Long {
        // 이미 존재하는 회원인지 확인
        val existingMember = memberExposedRepository.fetchByProviderAndProviderId(provider, providerId)
        if (existingMember != null) {
            return existingMember.id
        }

        // 신규 회원 저장
        return saveMember(attributes)
    }

    private fun saveMember(attributes: Map<String, Any>): Long {
        val provider = Provider.from(attributes["provider"].toString())
        val providerId = attributes["id"].toString()
        val email = attributes["email"].toString()
        val nickname = attributes["nickname"].toString()

        return memberExposedRepository.create(
            provider = provider,
            providerId = providerId,
            email = email,
            nickname = nickname
        )
    }
}