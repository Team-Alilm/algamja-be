package org.team_alilm.common.security

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.member.repository.MemberRepository

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    override fun loadUserByUsername(memberId: String): UserDetails {
        val member = memberRepository.findByIdOrNull(memberId.toLong())
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)

        return CustomMemberDetails(member = member)
    }

}