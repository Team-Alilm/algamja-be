package org.teamalilm.alilmbe.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByPhoneNumber(phoneNumber: String): MemberJpaEntity?
}