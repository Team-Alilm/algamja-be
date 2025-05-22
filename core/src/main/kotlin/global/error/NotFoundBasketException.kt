package org.team_alilm.global.error

class NotFoundBasketException : RuntimeException() {

    override val message: String
        get() { return "${ErrorCode.NOT_FOUND_BASKET.code} : ${ErrorCode.NOT_FOUND_BASKET.message}" }

}