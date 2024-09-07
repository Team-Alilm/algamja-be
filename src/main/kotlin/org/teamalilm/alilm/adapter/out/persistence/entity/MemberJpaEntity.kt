package org.teamalilm.alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilm.global.jpa.base.BaseEntity
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

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
    val provider: Provider,

    @Column(nullable = false)
    val providerId: Long,

    @Column(nullable = false, length = 30)
    var email: String,

    @Column(nullable = false, length = 10)
    var nickname: String,

    @Column
    var fcmToken: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseEntity()
