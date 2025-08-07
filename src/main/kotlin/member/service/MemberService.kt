package org.team_alilm.member.service

import domain.Member
import org.team_alilm.global.error.BusinessException
import org.team_alilm.global.error.ErrorCode
import org.team_alilm.member.repository.MemberRepository


class MemberService(
    private val memberRepository: MemberRepository
) {

    fun getMyInfo(memberId: Long) : Member {
        memberRepository.findById(memberId) ?: throw BusinessException(ErrorCode.NOT_FOUND_MEMBER)
    }
}