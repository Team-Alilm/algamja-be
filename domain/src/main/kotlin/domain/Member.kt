package domain

class Member(
    val id: MemberId? = null,
    val provider: Provider,
    val providerId: Long,
    var email: String,
    var nickname: String,
    ) {

    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(nickname.isNotBlank()) { "nickname must not be blank" }
    }

    fun changeInfo(newNickname: String, newEmail: String) {
        this.nickname = newNickname
        this.email = newEmail
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
