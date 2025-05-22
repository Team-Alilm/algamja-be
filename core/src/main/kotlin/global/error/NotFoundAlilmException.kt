package org.team_alilm.global.error

class NotFoundAlilmException : RuntimeException() {

    override val message: String
        get() { return "${ErrorCode.NOT_FOUND_ALILM.code} : ${ErrorCode.NOT_FOUND_ALILM.message}" }

}