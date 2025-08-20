package org.team_alilm.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.member.controller.dto.response.MyInfoResponse
import org.team_alilm.member.repository.MemberExposedRepository

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberExposedRepository: MemberExposedRepository
) {

    @Transactional
    fun updateMyInfo(memberId: Long, request: UpdateMyInfoRequest) {
        memberExposedRepository.updateMember(
            memberId = memberId,
            email = request.email,
            nickname = request.nickname
        )
    }
}