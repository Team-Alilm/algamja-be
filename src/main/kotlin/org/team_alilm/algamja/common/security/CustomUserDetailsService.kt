package org.team_alilm.algamja.common.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.member.repository.MemberExposedRepository

@Service
@Transactional(readOnly = true)
class CustomUserDetailsService(
    private val memberExposedRepository: MemberExposedRepository
) : UserDetailsService {

    override fun loadUserByUsername(memberId: String): UserDetails {
        val member = memberExposedRepository.fetchById(memberId.toLong())
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)

        return CustomMemberDetails(memberRow = member)
    }

}