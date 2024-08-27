package org.teamalilm.alilm.domain

import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

class Member(
    val id: MemberId? = null,
    val provider: Provider,
    val providerId: Long,
    val email: String,
    var phoneNumber: String,
    var nickname: String,
    var fcmToken: String? = null
    ) {

    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(phoneNumber.isNotBlank()) { "phoneNumber must not be blank" }
        require(nickname.isNotBlank()) { "nickname must not be blank" }
    }

    fun update(newNickname: String, newPhoneNumber: String) {
        this.nickname = newNickname
        this.phoneNumber = newPhoneNumber
    }

    data class MemberId(val value: Long)

}
