package org.teamalilm.alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.domain.FcmToken

@Component
class FcmTokenMapper {

    fun mapToJpaEntity(fcmToken: FcmToken, memberJpaEntity: MemberJpaEntity): FcmTokenJpaEntity {
        return FcmTokenJpaEntity(
            id = fcmToken.id?.value,
            token = fcmToken.token,
            member = memberJpaEntity,
        )
    }

}