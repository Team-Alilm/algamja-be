package org.teamalilm.alilmbe.application.service.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilmbe.application.port.out.member.LoadMemberPort
import org.teamalilm.alilmbe.common.error.NotFoundMemberException

@Service
@Transactional(readOnly = true)
class CustomUserDetailsService(
    private val loadMemberPort: LoadMemberPort
) : UserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val member = loadMemberPort.loadMember(id.toLong())
            ?: throw NotFoundMemberException()

        return CustomMemberDetails(member = member)
    }

}