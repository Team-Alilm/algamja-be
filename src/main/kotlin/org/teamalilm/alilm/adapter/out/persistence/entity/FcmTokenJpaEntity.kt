package org.teamalilm.alilm.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "fcm_token")
class FcmTokenJpaEntity(
    @Column(nullable = false, unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val memberId: MemberJpaEntity,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)