package org.teamalilm.alilmbe.global.security.service.oAuth2.data

class OAuth2Attribute(
    private val attributes: Map<String, Any>,
    private val provider: String,
    private val attributeKey: String,
    private val nickname: String,
    private val phoneNumber: String,
    private val _email: String,
) {

    private val email
        get() = _email

    fun convertToMap(): MutableMap<String, Any> {
        return HashMap<String, Any>().also {
            it["id"] = this.attributes[attributeKey] as Any
            it["provider"] = this.provider
            it["nickname"] = this.nickname
            it["phoneNumber"] = this.phoneNumber
            it["email"] = this.email
        }
    }

    fun toEntity(): Member {
        return Member(
            provider = Provider.from(this.provider),
            providerId = attributes[attributeKey] as Long,
            email = this._email,
            phoneNumber = this.phoneNumber,
            nickname = this.nickname,
            role = Role.MEMBER
        )
    }

    companion object {

        fun of(
            attributes: Map<String, Any>,
            provider: String,
            attributeKey: String
        ): OAuth2Attribute {
            return when (Provider.from(provider)) {
                Provider.KAKAO -> ofKakao(provider, attributeKey, attributes)
            }
        }

        private fun ofKakao(
            provider: String,
            attributeKey: String,
            attributes: Map<String, Any>
        ): OAuth2Attribute {
            val kakaoAccount = attributes["kakao_account"] as Map<*, *>
            val profile = attributes["properties"] as Map<*, *>
            val phoneNumber = kakaoAccount["phone_number"].toString()
            val nickname = profile["nickname"].toString()
            val email = kakaoAccount["email"].toString()

            return OAuth2Attribute(
                attributes = attributes,
                provider = provider,
                attributeKey = attributeKey,
                nickname = nickname,
                phoneNumber = phoneNumber,
                _email = email
            )
        }
    }
}