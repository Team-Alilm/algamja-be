package org.team_alilm.adapter.out.persistence.entity

import domain.Member
import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["provider", "provider_id"]
        ),
    ]

)
class MemberJpaEntity(
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
