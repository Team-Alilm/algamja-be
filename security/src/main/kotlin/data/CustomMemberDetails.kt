package org.team_alilm.data

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.team_alilm.domain.Member

/**
 * Custom implementation of UserDetails to integrate with Spring Security.
 */
class CustomMemberDetails(
    val member: Member
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority>? {
        // If you have roles or permissions, return them here.
        // Returning an empty list for simplicity.
        return null
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return member.id.toString()
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
