package org.team_alilm.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Notification(
    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val readYn: Boolean,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) { }