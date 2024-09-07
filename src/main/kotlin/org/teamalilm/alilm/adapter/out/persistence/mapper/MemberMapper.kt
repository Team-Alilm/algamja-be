package org.teamalilm.alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.domain.Member

@Component
class MemberMapper {

    fun mapToJpaEntity(member: Member) : MemberJpaEntity {
        return MemberJpaEntity(
            id = member.id?.value,
            email = member.email,
            nickname = member.nickname,
            providerId = member.providerId,
            provider = member.provider,
            fcmToken = member.fcmToken
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
            fcmToken = memberJpaEntity.fcmToken
        )
    }

    fun mapToDomainEntity(memberJpaEntity: MemberJpaEntity) : Member {
        return Member(
            id = Member.MemberId(memberJpaEntity.id!!),
            email = memberJpaEntity.email,
            nickname = memberJpaEntity.nickname,
            providerId = memberJpaEntity.providerId,
            provider = memberJpaEntity.provider,
            fcmToken = memberJpaEntity.fcmToken
        )
    }
}