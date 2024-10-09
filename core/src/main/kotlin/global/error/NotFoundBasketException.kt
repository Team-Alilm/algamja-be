package org.team_alilm.global.error

class NotFoundBasketException(
    val errorMessage: ErrorMessage
) : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_MEMBER.code} : ${ErrorMessage.NOT_FOUND_MEMBER.message}" }

}