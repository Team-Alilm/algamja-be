package org.team_alilm.config

enum class ApiPath(val pattern: String) {
    PRODUCTS_ALL("/api/v*/products/**"),                 // ✅ 버전명 포함된 products 경로
    ALILMS_COUNT("/api/v*/alilms/count"),                // ✅ 버전명 포함된 alilms/count
    ALILMS_RESTOCK_RANKING("/api/v*/alilms/restock/ranking"); // ✅ 버전명 포함된 ranking

    companion object {
        fun all(): List<String> = entries.map { it.pattern }
    }
}