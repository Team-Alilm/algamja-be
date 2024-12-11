package org.team_alilm.adapter.out.persistence.repository.basket

import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity

data class BasketAndMemberProjection(
    val basketJpaEntity: BasketJpaEntity,
    val memberJpaEntity: MemberJpaEntity,
    val fcmTokenJpaEntity: FcmTokenJpaEntity
)