package org.teamalilm.alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.FcmTokenJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.domain.Member

interface SpringDataFcmTokenRepository : JpaRepository<FcmTokenJpaEntity, Long> {

    fun findAllByMember(memberJpaEntity: MemberJpaEntity): List<FcmTokenJpaEntity>

}