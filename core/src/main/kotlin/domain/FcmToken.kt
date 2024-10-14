package org.team_alilm.domain

class FcmToken(
    val token: String,
    val memberId: Member.MemberId,
    val id: FcmTokenId? = null
) {
    init {
        require(token.isNotBlank()) { "token must not be blank" }
    }

    data class FcmTokenId(val value: Long)

}