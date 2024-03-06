package org.teamalilm.alilmbe.domain.member.error

class NotFoundMemberException(
    override val message: String?
) : IllegalStateException() {
}