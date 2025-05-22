package org.team_alilm.global.error

class NotFoundProductException: RuntimeException() {

    override val message: String
        get() { return "${Error.NOT_FOUND_PRODUCT.code} : ${Error.NOT_FOUND_PRODUCT.message}" }
}