package org.team_alilm.common.security.oauth

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.team_alilm.common.enums.Provider
import org.team_alilm.member.entity.Member
import org.team_alilm.member.repository.MemberRepository

@Service
class OauthLoginMemberService(
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun loginMember(provider: Provider, providerId: String, attributes: Map<String, Any>): Member {
        return memberRepository.findByProviderAndProviderId(provider, providerId)?: run {
            val newMember = saveMember(attributes)
            newMember
        }
    }

    private fun saveMember(attributes: Map<String, Any>): Member {
        val provider = Provider.from(attributes["provider"].toString())
        val providerId = attributes["id"].toString()
        val email = attributes["email"].toString()
        val nickname = attributes["nickname"].toString()

        return memberRepository.save(
            Member(
                provider = provider,
                providerId = providerId,
                email = email,
                nickname = nickname
            )
        )
    }
}