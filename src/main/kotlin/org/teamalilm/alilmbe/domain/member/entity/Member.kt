package org.teamalilm.alilmbe.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.teamalilm.alilmbe.global.jpa.base.BaseTimeEntity
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.OAuth2Provider

@Entity
class Member(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: OAuth2Provider,

    @Column(nullable = false)
    val providerId: Long,

    @Column(unique = true, nullable = false, length = 30)
    var email: String,

    @Column(nullable = false, length = 10)
    var nickname: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity() {

    fun update(email: String) {
        this.email = email
    }
}
