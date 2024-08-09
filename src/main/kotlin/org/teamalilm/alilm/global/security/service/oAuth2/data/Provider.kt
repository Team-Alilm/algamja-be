package org.teamalilm.alilm.global.security.service.oAuth2.data

enum class Provider {

    KAKAO;

    companion object {
        fun from(provider: String): Provider {
            return Provider.valueOf(provider.uppercase())
        }
    }
}