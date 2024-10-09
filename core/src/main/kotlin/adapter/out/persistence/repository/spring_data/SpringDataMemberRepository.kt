package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

interface SpringDataMemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByIsDeleteFalseAndProviderAndProviderId(provider: Provider, providerId: Long): MemberJpaEntity?
}