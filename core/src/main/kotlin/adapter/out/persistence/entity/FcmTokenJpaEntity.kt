package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(name = "fcm_token")
class FcmTokenJpaEntity(
    @Column(nullable = false, unique = true)
    val token: String,

    @Column(name = "member_id", nullable = false)
    val memberJpaEntityId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity()
