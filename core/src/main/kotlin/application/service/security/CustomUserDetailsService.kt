package org.team_alilm.application.service.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails
import org.team_alilm.application.port.out.LoadMemberPort
import org.teamalilm.alilm.common.error.NotFoundMemberException

@Service
@Transactional(readOnly = true)
class CustomUserDetailsService(
    private val loadMemberPort: org.team_alilm.application.port.out.LoadMemberPort
) : UserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val member = loadMemberPort.loadMember(id.toLong())
            ?: throw NotFoundMemberException()

        return CustomMemberDetails(member = member)
    }

}