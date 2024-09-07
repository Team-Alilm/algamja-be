package org.teamalilm.alilm.domain

import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

class Member(
    val id: MemberId? = null,
    val provider: Provider,
    val providerId: Long,
    val email: String,
    var nickname: String,
    var fcmToken: String? = null
    ) {

    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(nickname.isNotBlank()) { "nickname must not be blank" }
    }

    fun update(newNickname: String) {
        this.nickname = newNickname
    }

    data class MemberId(val value: Long)

}
