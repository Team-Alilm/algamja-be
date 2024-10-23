package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity

interface SpringDataFcmTokenRepository : JpaRepository<FcmTokenJpaEntity, Long> {

    fun findByMemberId(memberId: Long): List<FcmTokenJpaEntity>
    fun findByToken(token: String): FcmTokenJpaEntity?

}