package org.team_alilm.global.error

class NotFoundAlilmException : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_ALILM.code} : ${ErrorMessage.NOT_FOUND_ALILM.message}" }

}