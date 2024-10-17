package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.Member

@Component
class FcmTokenMapper {

    fun mapToJpaEntity(fcmToken: FcmToken, memberJpaEntityId: Long): FcmTokenJpaEntity {
        return FcmTokenJpaEntity(
            id = fcmToken.id?.value,
            token = fcmToken.token,
            memberJpaEntityId = memberJpaEntityId,
        )
    }

    fun mapToDomain(fcmTokenJpaEntity: FcmTokenJpaEntity): FcmToken {
        return FcmToken(
            id = FcmToken.FcmTokenId(fcmTokenJpaEntity.id!!),
            token = fcmTokenJpaEntity.token,
            memberId = Member.MemberId(fcmTokenJpaEntity.memberJpaEntityId),
        )
    }

}