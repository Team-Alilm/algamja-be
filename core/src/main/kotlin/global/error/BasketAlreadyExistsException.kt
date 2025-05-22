package org.team_alilm.global.error

class BasketAlreadyExistsException : RuntimeException() {

    override val message: String
        get() { return Error.BASKET_ALREADY_EXISTS.message }
}