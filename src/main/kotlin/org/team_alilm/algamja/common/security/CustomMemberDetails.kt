package org.team_alilm.algamja.common.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.member.entity.MemberRow

/**
 * Custom implementation of UserDetails to integrate with Spring Security.
 */
class CustomMemberDetails(
    val memberRow: MemberRow
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
        return memberRow.id.toString()
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

fun CustomMemberDetails.requireMemberId(): Long {
    return this.memberRow.id ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)
}
