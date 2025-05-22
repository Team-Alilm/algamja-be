package org.team_alilm.global.error

class NotFoundAlilmException : RuntimeException() {

    override val message: String
        get() { return "${Error.NOT_FOUND_ALILM.code} : ${Error.NOT_FOUND_ALILM.message}" }

}