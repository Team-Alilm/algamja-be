package org.team_alilm.common.enum

enum class Provider {

    KAKAO;

    companion object {
        fun from(provider: String): Provider {
            return values().firstOrNull { it.name.equals(provider, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown provider: $provider")
        }
    }
}