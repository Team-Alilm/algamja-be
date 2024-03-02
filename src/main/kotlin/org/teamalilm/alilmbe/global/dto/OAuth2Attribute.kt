package org.teamalilm.alilmbe.global.dto

import org.teamalilm.alilmbe.global.status.OAuth2Provider
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.*

class OAuth2Attribute(
    private val attributes: Map<String, Any>,
    private val provider: String,
    private val attributeKey: String,
    private val nickname: String,
    private val email: String
) {
    fun convertToMap(): MutableMap<String, Any> {
        return HashMap<String, Any>().also {
            it["attributes"] = this.attributes
            it["provider"] = this.provider
            it["attributeKey"] = this.attributeKey
            it["nickname"] = this.nickname
            it["email"] = this.email
        }
    }

    companion object {

        fun of(attributes : Map<String, Any>, provider: String, attributeKey: String) : OAuth2Attribute {
            return when (OAuth2Provider.from(provider)) {
                OAuth2Provider.KAKAO -> ofKakao(provider, attributeKey, attributes)
            }
        }

        fun ofKakao(provider: String, attributeKey: String, attributes: Map<String, Any>) : OAuth2Attribute {
            val kakaoAccount = attributes.get("kakao_account") as Map<String, Any>
            val profile = attributes.get("profile") as Map<String, Any>

            val nickname = profile.get("nickname").toString()
            val email = kakaoAccount.get("email").toString()

            return OAuth2Attribute(
                attributes = attributes,
                provider = provider,
                attributeKey = attributeKey,
                nickname = nickname,
                email = email
            )
        }
    }
}