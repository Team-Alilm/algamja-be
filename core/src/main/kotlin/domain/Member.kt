package org.team_alilm.domain

class Member(
    val id: MemberId? = null,
    val provider: Provider,
    val providerId: Long,
    val email: String,
    var nickname: String,
    ) {

    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(nickname.isNotBlank()) { "nickname must not be blank" }
    }

    fun update(newNickname: String) {
        this.nickname = newNickname
    }

    data class MemberId(val value: Long)

    enum class Provider {

        KAKAO;

        companion object {
            fun from(provider: String): Provider {
                return Provider.valueOf(provider.uppercase())
            }
        }
    }

}
