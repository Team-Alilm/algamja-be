package org.teamalilm.alilm.common.error

class BasketAlreadyExistsException(
    private val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return errorMessage.message }
}