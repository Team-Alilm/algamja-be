package org.team_alilm.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.team_alilm.common.enums.Provider
import org.team_alilm.common.jpa.base.BaseEntity

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["provider", "provider_id"]
        ),
    ]
)
class Member(

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("회원 가입 경로 제공자 (예: GOOGLE, KAKAO, NAVER)")
    val provider: Provider,

    @Column(nullable = false)
    @Comment("제공자별 회원 식별 ID")
    val providerId: String,

    @Column(nullable = false, length = 30)
    @Comment("회원 이메일")
    var email: String,

    @Column(nullable = false, length = 10)
    @Comment("회원 닉네임")
    var nickname: String,
) : BaseEntity<Long>()