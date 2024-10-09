package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long>