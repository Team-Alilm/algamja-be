package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.team_alilm.domain.Member

@Component
class MemberMapper {

    fun wmapToJpaEntity(member: Member) : MemberJpaEntity {
        return MemberJpaEntity(
            id = member.id?.value,
            email = member.email,
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
            nickname = memberJpaEntity.nickname,
            providerId = memberJpaEntity.providerId,
            provider = memberJpaEntity.provider,
        )
    }

    fun mapToDomainEntity(memberJpaEntity: MemberJpaEntity) : Member {
        return Member(
            id = Member.MemberId(memberJpaEntity.id!!),
            email = memberJpaEntity.email,
            nickname = memberJpaEntity.nickname,
            providerId = memberJpaEntity.providerId,
            provider = memberJpaEntity.provider,
        )
    }
}