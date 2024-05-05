package org.teamalilm.alilmbe.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.domain.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByPhoneNumber(phoneNumber: String): Member?
}