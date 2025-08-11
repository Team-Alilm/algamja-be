package org.team_alilm.common.security.oauth

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.team_alilm.common.enums.Provider
import org.team_alilm.member.entity.Member
import org.team_alilm.member.repository.MemberRepository

@Service
@Transactional(readOnly = true)
class OauthLoginMemberService(
    private val memberRepository: MemberRepository,
    private val addMemberPort: AddMemberPort,
    private val addMemberRoleMappingPort: AddMemberRoleMappingPort,
    private val loadRolePort: LoadRolePort
) {

    @Transactional
    fun loginMember(provider: Provider, providerId: String, attributes: Map<String, Any>): Member {
        return memberRepository.findByProviderAndProviderId(provider, providerId)?: run {
            val newMember = saveMember(attributes)
            saveMemberRoleMapping(newMember)
            newMember
        }
    }

    private fun saveMember(attributes: Map<String, Any>): Member {
        val provider = Member.Provider.from(attributes["provider"].toString())
        val providerId = attributes["id"].toString().toLong()
        val email = attributes["email"].toString()
        val nickname = attributes["nickname"].toString()

        return addMemberPort.addMember(Member(
            id = null,
            provider = provider,
            providerId = providerId,
            email = email,
            nickname = nickname))
    }

    private fun saveMemberRoleMapping(member: Member) {
        val role = loadRolePort.loadRole(Role.RoleType.ROLE_USER) ?: throw NotFoundRoleException()
        addMemberRoleMappingPort.addMemberRoleMapping(member, role)
    }

}