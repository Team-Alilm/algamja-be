package org.team_alilm.domain

class FcmToken(
    val token: String,
    val memberId: Member.MemberId,
    val id: FcmTokenId? = null
) {

    data class FcmTokenId(val value: Long)

}