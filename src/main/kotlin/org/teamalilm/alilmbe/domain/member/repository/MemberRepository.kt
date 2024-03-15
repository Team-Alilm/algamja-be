package org.teamalilm.alilmbe.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.global.status.OAuth2Provider
import org.teamalilm.alilmbe.domain.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: String): Member?
}