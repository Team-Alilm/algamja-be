package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity
import domain.Member

interface SpringDataMemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByIsDeleteFalseAndProviderAndProviderId(provider: Member.Provider, providerId: Long): MemberJpaEntity?
}