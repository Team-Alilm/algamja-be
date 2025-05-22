package org.team_alilm.global.error

class NotFoundMemberException : RuntimeException() {

    override val message: String
        get() { return "${ErrorCode.NOT_FOUND_MEMBER.code} : ${ErrorCode.NOT_FOUND_MEMBER.message}" }

}