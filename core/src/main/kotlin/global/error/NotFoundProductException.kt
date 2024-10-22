package org.team_alilm.global.error

class NotFoundProductException: RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_PRODUCT.code} : ${ErrorMessage.NOT_FOUND_PRODUCT.message}" }
}