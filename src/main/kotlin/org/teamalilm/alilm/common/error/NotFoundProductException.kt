package org.teamalilm.alilm.common.error

class NotFoundProductException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_PRODUCT.code} : ${ErrorMessage.NOT_FOUND_PRODUCT.message}" }
}