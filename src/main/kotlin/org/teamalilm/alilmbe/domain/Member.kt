package org.teamalilm.alilmbe.domain

import org.teamalilm.alilmbe.global.security.service.oAuth2.data.Provider

class Member(
    val id: MemberId?,
    val provider: Provider,
    val providerId: Long,
    val email: String,
    var phoneNumber: String,
    var nickname: String,
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
