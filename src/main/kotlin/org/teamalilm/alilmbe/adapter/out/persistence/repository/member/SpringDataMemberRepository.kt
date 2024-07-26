package org.teamalilm.alilmbe.adapter.out.persistence.repository.member

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity

interface SpringDataMemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByPhoneNumberAndIsDeleteFalse(phoneNumber: String): MemberJpaEntity?
}