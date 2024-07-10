package org.teamalilm.alilmbe.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.jpa.entity.member.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByPhoneNumber(phoneNumber: String): Member?
}