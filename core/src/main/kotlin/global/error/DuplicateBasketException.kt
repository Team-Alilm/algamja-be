package org.team_alilm.global.error

class DuplicateBasketException : RuntimeException() {

    override val message: String
        get() { return ErrorCode.DUPLICATE_BASKET.code + " : " + ErrorCode.DUPLICATE_BASKET.message }
}