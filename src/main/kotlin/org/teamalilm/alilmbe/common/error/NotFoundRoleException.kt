package org.teamalilm.alilmbe.common.error

class NotFoundRoleException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return errorMessage.message }
}