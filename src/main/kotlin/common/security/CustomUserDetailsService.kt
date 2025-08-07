package org.team_alilm.common.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.team_alilm.application.port.out.LoadMemberPort
import org.team_alilm.global.error.NotFoundMemberException

@Service
class CustomUserDetailsService(
    private val loadMemberPort: LoadMemberPort
) : UserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val member = loadMemberPort.loadMember(id.toLong())
            ?: throw NotFoundMemberException()

        return CustomMemberDetails(member = member)
    }

}