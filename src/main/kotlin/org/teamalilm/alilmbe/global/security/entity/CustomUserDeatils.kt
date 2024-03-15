package org.teamalilm.alilmbe.global.security.entity

import java.util.Collections
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.teamalilm.alilmbe.domain.member.entity.Member

class CustomUserDetails(
    private val member: Member
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Collections.emptyList()
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return this.member.id.toString()
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