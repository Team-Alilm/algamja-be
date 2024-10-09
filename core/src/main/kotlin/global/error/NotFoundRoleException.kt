package org.team_alilm.global.error

class NotFoundRoleException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return errorMessage.message }
}