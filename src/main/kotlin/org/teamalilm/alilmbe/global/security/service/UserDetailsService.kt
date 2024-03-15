package org.teamalilm.alilmbe.global.security.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.member.repository.MemberRepository
import org.teamalilm.alilmbe.global.security.entity.CustomUserDetails

@Component
@Transactional(readOnly = true)
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    override fun loadUserByUsername(memberId: String): UserDetails? {
        val member = memberRepository.findByIdOrNull(memberId.toLong())
            ?: throw IllegalArgumentException("존재하지 않는 회원입니다.")

        return CustomUserDetails(member)
    }
}