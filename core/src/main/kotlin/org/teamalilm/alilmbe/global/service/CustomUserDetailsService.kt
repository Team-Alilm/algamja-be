package org.teamalilm.alilmbe.global.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.global.entity.CustomUserDetails
import org.teamalilm.alilmbe.domain.member.error.NotFoundMemberException
import org.teamalilm.alilmbe.domain.member.repository.MemberRepository

@Component
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    override fun loadUserByUsername(memberId: String): UserDetails? {
        return null
    }

    fun loadUserByMemberId(memberId: Long): UserDetails {
        val member = memberRepository.findByIdOrNull(memberId.toLong()) ?: (throw NotFoundMemberException(""))

        return CustomUserDetails(member)
    }
}