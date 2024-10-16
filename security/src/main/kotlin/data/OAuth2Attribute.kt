package org.team_alilm.data

import org.team_alilm.domain.Member

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

    companion object {

        fun of(
            attributes: Map<String, Any>,
            provider: String,
            attributeKey: String
        ): OAuth2Attribute {
            return when (Member.Provider.from(provider)) {
                Member.Provider.KAKAO -> ofKakao(provider, attributeKey, attributes)
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