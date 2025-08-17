package org.team_alilm.common.security

enum class PublicApiPaths(val pattern: String) {
    PRODUCTS_ALL("/api/v*/products/**");               // ✅ 버전명 포함된 products 경로

    companion object {
        fun all(): List<String> = entries.map { it.pattern }
    }
}