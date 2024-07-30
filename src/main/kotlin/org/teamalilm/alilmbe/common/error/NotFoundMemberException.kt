package org.teamalilm.alilmbe.common.error

class NotFoundMemberException : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_MEMBER.code} : ${ErrorMessage.NOT_FOUND_MEMBER.message}" }

}