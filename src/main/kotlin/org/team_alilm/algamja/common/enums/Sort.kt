package org.team_alilm.algamja.common.enums

enum class Sort {

    WAITING_COUNT_DESC,
    CREATED_DATE_DESC,
    PRICE_ASC,
    PRICE_DESC;

    companion object {
        fun from(sort: String): Sort {
            return Sort.entries.firstOrNull { it.name.equals(sort, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown sort type: $sort")
        }
    }
}