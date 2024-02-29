package org.teamalilm.alilmbe.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun existsByLoginId(loginId: String): Boolean

    fun findByLoginId(loginId: String): Member?
}