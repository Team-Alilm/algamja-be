package org.team_alilm.global.error

class NotFoundRoleException(
) : RuntimeException() {

    override val message: String
        get() { return Error.NOT_FOUND_ROLE.message }
}