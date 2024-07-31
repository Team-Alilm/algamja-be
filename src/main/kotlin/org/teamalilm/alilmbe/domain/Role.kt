package org.teamalilm.alilmbe.domain

class Role (
    val id: RoleId? = null,
    val roleType: RoleType
) {

    data class RoleId (
        val id: Long
    )

    enum class RoleType {

        ROLE_ADMIN,
        ROLE_USER,
        ROLE_GUEST

    }
}