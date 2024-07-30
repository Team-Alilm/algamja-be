package org.teamalilm.alilmbe.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.Provider

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["provider", "provider_id"]
        ),
        UniqueConstraint(
            columnNames = ["phone_number"]
        ),
    ]

)
class MemberJpaEntity(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @Column(nullable = false)
    val providerId: Long,

    @Column(nullable = false, length = 30)
    var email: String,

    @Column(nullable = false, length = 20, unique = true)
    var phoneNumber: String,

    @Column(nullable = false, length = 10)
    var nickname: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseEntity()
