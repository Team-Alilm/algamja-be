package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.domain.FcmToken
import org.teamalilm.alilm.domain.Member

@Component
class FcmTokenMapper {

    fun mapToJpaEntity(fcmToken: FcmToken, memberJpaEntity: MemberJpaEntity): FcmTokenJpaEntity {
        return FcmTokenJpaEntity(
            id = fcmToken.id?.value,
            token = fcmToken.token,
            member = memberJpaEntity,
        )
    }

    fun mapToDomain(fcmTokenJpaEntity: FcmTokenJpaEntity): FcmToken {
        return FcmToken(
            id = FcmToken.FcmTokenId(fcmTokenJpaEntity.id!!),
            token = fcmTokenJpaEntity.token,
            memberId = Member.MemberId(fcmTokenJpaEntity.member.id!!),
        )
    }

}