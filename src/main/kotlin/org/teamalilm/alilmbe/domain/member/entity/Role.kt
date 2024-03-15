package org.teamalilm.alilmbe.domain.member.entity

enum class Role(
    val key: String
) {
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_USER"),
    GUEST("ROLE_GUEST")
}