package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(
    name = "fcm_token",
    indexes = [
        Index(
            name = "idx_member_id",
            columnList = "member_id"
        )
    ]
)
class FcmTokenJpaEntity(
    @Column(nullable = false, unique = true)
    val token: String,

    @Column(nullable = false)
    val memberId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null
) : BaseTimeEntity()
