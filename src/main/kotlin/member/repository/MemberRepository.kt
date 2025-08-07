package org.team_alilm.member.repository

import domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByIsDeleteFalseAndProviderAndProviderId(provider: Member.Provider, providerId: Long): MemberJpaEntity?
}