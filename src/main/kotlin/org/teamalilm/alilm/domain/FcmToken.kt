package org.teamalilm.alilm.domain

class FcmToken(
    val token: String,
    val member: Member,
    val id: FcmTokenId? = null
) {
    init {
        require(token.isNotBlank()) { "token must not be blank" }
    }

    data class FcmTokenId(val value: Long)

}