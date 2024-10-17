package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.LoginMemberUseCase
import org.team_alilm.application.port.out.AddMemberPort
import org.team_alilm.application.port.out.AddMemberRoleMappingPort
import org.team_alilm.application.port.out.LoadMemberPort
import org.team_alilm.application.port.out.LoadRolePort
import org.team_alilm.domain.Member
import org.team_alilm.domain.Role
import org.team_alilm.global.error.NotFoundRoleException

@Service
@Transactional(readOnly = true)
class OauthLoginMemberService(
    private val loadMemberPort: LoadMemberPort,
    private val addMemberPort: AddMemberPort,
    private val addMemberRoleMappingPort: AddMemberRoleMappingPort,
    private val loadRolePort: LoadRolePort
) : LoginMemberUseCase {

    @Transactional
    override fun loginMember(provider: Member.Provider, providerId: String, attributes: Map<String, Any>): Member {
        return loadMemberPort.loadMember(provider, providerId)?: run {
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
