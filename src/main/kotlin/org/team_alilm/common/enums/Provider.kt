package org.team_alilm.common.enums

enum class Provider {

    KAKAO;

    companion object {
        fun from(provider: String): Provider {
            return entries.firstOrNull { it.name.equals(provider, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown provider: $provider")
        }
    }
}