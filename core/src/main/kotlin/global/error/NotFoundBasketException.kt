package org.team_alilm.global.error

class NotFoundBasketException : RuntimeException() {

    override val message: String
        get() { return "${Error.NOT_FOUND_BASKET.code} : ${Error.NOT_FOUND_BASKET.message}" }

}