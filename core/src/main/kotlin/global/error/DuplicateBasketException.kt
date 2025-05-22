package org.team_alilm.global.error

class DuplicateBasketException : RuntimeException() {

    override val message: String
        get() { return Error.DUPLICATE_BASKET.code + " : " + Error.DUPLICATE_BASKET.message }
}