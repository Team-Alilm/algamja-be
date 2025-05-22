package org.team_alilm.global.error

class NotFoundRoleException(
) : RuntimeException() {

    override val message: String
        get() { return ErrorCode.NOT_FOUND_ROLE.message }
}