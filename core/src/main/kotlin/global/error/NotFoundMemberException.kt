package org.team_alilm.global.error

class NotFoundMemberException : RuntimeException() {

    override val message: String
        get() { return "${Error.NOT_FOUND_MEMBER.code} : ${Error.NOT_FOUND_MEMBER.message}" }

}