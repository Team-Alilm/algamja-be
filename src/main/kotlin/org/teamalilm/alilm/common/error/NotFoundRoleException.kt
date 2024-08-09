package org.teamalilm.alilm.common.error

class NotFoundRoleException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return errorMessage.message }
}