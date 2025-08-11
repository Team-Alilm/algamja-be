package org.team_alilm.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.member.controller.dto.response.MyInfoResponse
import org.team_alilm.member.repository.MemberRepository

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {

    fun getMyInfo(memberId: Long) : MyInfoResponse =
        memberRepository.findById(memberId)
            .orElseThrow { BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR) }
            .let { member ->
                MyInfoResponse(
                    email = member.email,
                    nickname = member.nickname
                )
            }

    @Transactional
    fun updateMyInfo(memberId: Long, request: UpdateMyInfoRequest) {
        val member = memberRepository.findById(memberId)
            .orElseThrow { BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR) }

        member.email = request.email
        member.nickname = request.nickname

        memberRepository.save(member)
    }
}