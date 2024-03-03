package org.teamalilm.alilmbe.global.status

enum class OAuth2Provider {

    KAKAO;

    companion object {
        fun from(provider: String) : OAuth2Provider {
                return OAuth2Provider.valueOf(provider.uppercase())
        }
    }
}