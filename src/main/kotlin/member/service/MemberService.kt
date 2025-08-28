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

    fun getMyInfo(memberId: Long): MyInfoResponse {
        val member = memberExposedRepository.fetchById(memberId)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)
        
        return MyInfoResponse(
            id = member.id!!,
            email = member.email,
            nickname = member.nickname,
            provider = member.provider.name
        )
    }

    @Transactional
    fun updateMyInfo(memberId: Long, request: UpdateMyInfoRequest) {
        memberExposedRepository.fetchById(memberId)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)
            
        memberExposedRepository.updateMember(
            nickname = request.nickname,
            email = request.email,
            memberId = memberId
        )
    }
}