package org.team_alilm.global.error

class NotFoundBasketException : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_BASKET.code} : ${ErrorMessage.NOT_FOUND_BASKET.message}" }

}