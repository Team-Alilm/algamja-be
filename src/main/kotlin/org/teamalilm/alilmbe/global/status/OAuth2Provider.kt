package org.teamalilm.alilmbe.global.status

import java.util.*

enum class OAuth2Provider {

    KAKAO;

    companion object {
        fun from(provider: String) : OAuth2Provider {
                return OAuth2Provider.valueOf(provider.uppercase(Locale.getDefault()))
        }
    }
}