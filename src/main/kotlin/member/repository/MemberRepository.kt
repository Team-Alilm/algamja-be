package org.team_alilm.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.common.enums.Provider
import org.team_alilm.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): Member?
}