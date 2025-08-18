package org.team_alilm.common.security

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.member.repository.MemberExposedRepository

@Service
class CustomUserDetailsService(
    private val memberExposedRepository: MemberExposedRepository
) : UserDetailsService {

    override fun loadUserByUsername(memberId: String): UserDetails {
        val member = memberExposedRepository.findByIdOrNull(memberId.toLong())
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)

        return CustomMemberDetails(member = member)
    }

}