package org.team_alilm.global.error

class NotFoundProductException: RuntimeException() {

    override val message: String
        get() { return "${ErrorCode.NOT_FOUND_PRODUCT.code} : ${ErrorCode.NOT_FOUND_PRODUCT.message}" }
}