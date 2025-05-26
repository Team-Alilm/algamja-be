package org.team_alilm.config

enum class ApiPath(val pattern: String) {
    PRODUCTS_ALL("/api/**/products/**"),
    ALILMS_COUNT("/api/**/alilms/count"),
    ALILMS_RESTOCK_RANKING("/api/**/alilms/restock/ranking");

    companion object {
        fun all(): List<String> = values().map { it.pattern }
    }
}