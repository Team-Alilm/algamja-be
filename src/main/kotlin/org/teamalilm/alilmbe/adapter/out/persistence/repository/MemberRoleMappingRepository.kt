package org.teamalilm.alilmbe.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberRoleMappingJpaEntity

interface MemberRoleMappingRepository : JpaRepository<MemberRoleMappingJpaEntity, Long> {
}