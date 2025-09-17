package org.team_alilm.algamja.common.security

enum class PublicApiPaths(val pattern: String) {
    PRODUCTS_ALL("/api/v*/products/**"),
    PRODUCTS_DETAIL("/api/v*/products/*"),
    PRODUCTS_SIMILAR("/api/v*/products/similar/*"),
    PRODUCTS_RECENTLY_RESTOCKED("/api/v*/products/recently-restocked"),
    BANNERS("/api/v*/banners"),
    ;

    companion object {
        fun all(): List<String> = entries.map { it.pattern }
    }
}