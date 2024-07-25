package org.teamalilm.alilmbe.domain.member

import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.Provider

class Member(
    val id: MemberId,
    val provider: Provider,
    val providerId: Long,
    val email: String,
    val phoneNumber: String,
    val nickname: String,
    val role: MemberJpaEntity.Role,
    ) {

    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(phoneNumber.isNotBlank()) { "phoneNumber must not be blank" }
        require(nickname.isNotBlank()) { "nickname must not be blank" }
    }

    data class MemberId(val value: Long?)
}
