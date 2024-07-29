package org.teamalilm.alilmbe.adapter.out.persistence.entity.member

import jakarta.persistence.*
import java.util.Collections
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.teamalilm.alilmbe.domain.member.Member.*
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseEntity(), UserDetails {

    fun updatePhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Collections.emptyList()
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return this.id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }


}
