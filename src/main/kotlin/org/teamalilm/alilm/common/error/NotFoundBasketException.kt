package org.teamalilm.alilm.common.error

class NotFoundBasketException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_MEMBER.code} : ${ErrorMessage.NOT_FOUND_MEMBER.message}" }

}