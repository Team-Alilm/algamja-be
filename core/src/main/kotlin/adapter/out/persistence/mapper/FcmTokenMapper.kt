package org.team_alilm.adapter.out.persistence.mapper

import domain.FcmToken
import domain.Member
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.FcmTokenJpaEntity

@Component
class FcmTokenMapper {

    fun mapToJpaEntity(fcmToken: FcmToken, memberId: Long): FcmTokenJpaEntity {
        return FcmTokenJpaEntity(
            id = fcmToken.id?.value,
            token = fcmToken.token,
            memberId = memberId,
        )
    }

    fun mapToDomain(fcmTokenJpaEntity: FcmTokenJpaEntity): FcmToken {
        return FcmToken(
            id = FcmToken.FcmTokenId(fcmTokenJpaEntity.id!!),
            token = fcmTokenJpaEntity.token,
            memberId = Member.MemberId(fcmTokenJpaEntity.memberId),
        )
    }

}