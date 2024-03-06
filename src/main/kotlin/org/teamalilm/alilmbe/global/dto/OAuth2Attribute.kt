package org.teamalilm.alilmbe.global.dto

import lombok.extern.slf4j.Slf4j
import org.teamalilm.alilmbe.global.status.OAuth2Provider
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.member.entity.Role
import java.util.*

@Slf4j
class OAuth2Attribute(
    private val attributes: Map<String, Any>,
    private val provider: String,
    private val attributeKey: String,
    private val nickname: String,
    private val _email: String,
) {

    val email
        get() = _email

    fun convertToMap(): MutableMap<String, Any> {
        return HashMap<String, Any>().also {
            it["id"] = this.attributes[attributeKey] as Any
            it["provider"] = this.provider
            it["nickname"] = this.nickname
            it["email"] = this.email
        }
    }

    fun toEntity(): Member {
        return Member(
            provider = OAuth2Provider.from(this.provider),
            providerId = attributes[attributeKey] as Long,
            email = this._email,
            nickname = this.nickname,
            role = Role.MEMBER
        )
    }

    companion object {

        fun of(attributes : Map<String, Any>, provider: String, attributeKey: String) : OAuth2Attribute {
            return when (OAuth2Provider.from(provider)) {
                OAuth2Provider.KAKAO -> ofKakao(provider, attributeKey, attributes)
            }
        }

        private fun ofKakao(provider: String, attributeKey: String, attributes: Map<String, Any>) : OAuth2Attribute {
            val kakaoAccount = attributes["kakao_account"] as Map<String, Any>
            val profile = attributes["properties"] as Map<String, Any>

            val nickname = profile["nickname"].toString()
            val email = kakaoAccount["email"].toString()

            return OAuth2Attribute(
                attributes = attributes,
                provider = provider,
                attributeKey = attributeKey,
                nickname = nickname,
                _email = email
            )
        }
    }
}