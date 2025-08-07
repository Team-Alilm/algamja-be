package org.team_alilm.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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
    val provider: Member.Provider,

    @Column(nullable = false)
    val providerId: Long,

    @Column(nullable = false, length = 30)
    var email: String,

    @Column(nullable = false, length = 10)
    var nickname: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity()