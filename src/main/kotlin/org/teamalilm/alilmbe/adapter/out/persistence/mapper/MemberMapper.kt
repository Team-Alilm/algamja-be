package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.domain.member.Member

@Component
class MemberMapper {

    fun mapToJpaEntity(member: Member) : MemberJpaEntity {
        return MemberJpaEntity(
            id = member.id?.value,
            email = member.email,
            phoneNumber = member.phoneNumber,
            nickname = member.nickname,
            providerId = member.providerId,
            provider = member.provider,
        )
    }

    fun mapToDomainEntityOrNull(memberJpaEntity: MemberJpaEntity?) : Member? {
        memberJpaEntity ?: return null

        return Member(
            id = Member.MemberId(memberJpaEntity.id!!),
            email = memberJpaEntity.email,
            phoneNumber = memberJpaEntity.phoneNumber,
            nickname = memberJpaEntity.nickname,
            providerId = memberJpaEntity.providerId,
            provider = memberJpaEntity.provider
        )
    }

    fun mapToDomainEntity(memberJpaEntity: MemberJpaEntity) : Member {
        return Member(
            id = Member.MemberId(memberJpaEntity.id!!),
            email = memberJpaEntity.email,
            phoneNumber = memberJpaEntity.phoneNumber,
            nickname = memberJpaEntity.nickname,
            providerId = memberJpaEntity.providerId,
            provider = memberJpaEntity.provider
        )
    }
}