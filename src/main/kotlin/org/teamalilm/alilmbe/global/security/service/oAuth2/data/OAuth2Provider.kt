package org.teamalilm.alilmbe.global.security.service.oAuth2.data

enum class OAuth2Provider {

    KAKAO;

    companion object {
        fun from(provider: String): OAuth2Provider {
            return OAuth2Provider.valueOf(provider.uppercase())
        }
    }
}