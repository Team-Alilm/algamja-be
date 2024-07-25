package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity
import org.teamalilm.alilmbe.domain.member.Member

@Component
class MemberMapper {

    fun mapToJpaEntity(member: Member) : MemberJpaEntity {
        return MemberJpaEntity(
            id = member.id.value,
            email = member.email,
            phoneNumber = member.phoneNumber,
            nickname = member.nickname,
            providerId = member.providerId,
            provider = member.provider,
            role = member.role
        )
    }
}