package org.teamalilm.alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.FcmTokenJpaEntity

interface SpringDataFcmTokenRepository : JpaRepository<FcmTokenJpaEntity, Long> {
}