package org.team_alilm.algamja.common.enums

enum class Provider {

    KAKAO;

    companion object {
        fun from(provider: String): Provider {
            return entries.firstOrNull { it.name.equals(provider, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown provider: $provider")
        }
    }
}