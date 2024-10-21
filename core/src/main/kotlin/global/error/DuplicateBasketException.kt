package org.team_alilm.global.error

class DuplicateBasketException : RuntimeException() {

    override val message: String
        get() { return ErrorMessage.DUPLICATE_BASKET.code + " : " + ErrorMessage.DUPLICATE_BASKET.message }
}