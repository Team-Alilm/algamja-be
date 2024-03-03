package org.teamalilm.alilmbe.member.entity

enum class Role(
    val key: String
) {
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_USER"),
    GUEST("ROLE_GUEST")
}