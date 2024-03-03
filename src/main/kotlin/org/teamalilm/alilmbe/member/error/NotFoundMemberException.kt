package org.teamalilm.alilmbe.member.error

class NotFoundMemberException(
    override val message: String?
) : IllegalStateException() {
}