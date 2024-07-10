package org.teamalilm.alilmbe.adapter.out.persistence.jpa.entity.member

enum class Role(
    val key: String
) {
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_USER"),
    GUEST("ROLE_GUEST")
}