package org.team_alilm.algamja.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.algamja.member.controller.dto.response.MyInfoResponse
import org.team_alilm.algamja.member.controller.dto.response.UserStatisticsResponse
import org.team_alilm.algamja.member.repository.MemberExposedRepository
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.notification.repository.NotificationExposedRepository

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberExposedRepository: MemberExposedRepository,
    private val basketExposedRepository: BasketExposedRepository,
    private val notificationExposedRepository: NotificationExposedRepository
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

    fun getMyStatistics(memberId: Long): UserStatisticsResponse {
        memberExposedRepository.fetchById(memberId)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)

        val registeredProductCount = basketExposedRepository.countActiveBasketsByMemberId(memberId)
        val receivedNotificationCount = notificationExposedRepository.countNotificationsByMemberId(memberId)

        return UserStatisticsResponse(
            registeredProductCount = registeredProductCount,
            receivedNotificationCount = receivedNotificationCount
        )
    }
}