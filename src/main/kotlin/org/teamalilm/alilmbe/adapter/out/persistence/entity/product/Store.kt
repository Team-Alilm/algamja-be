package org.teamalilm.alilmbe.adapter.out.persistence.entity.product

enum class Store {

    MUSINSA, ZIGZAG, OLIVEYOUNG;

    companion object {
        fun fromString(value: String): Store {
            return when (value) {
                "MUSINSA" -> MUSINSA
                "ZIGZAG" -> ZIGZAG
                "OLIVEYOUNG" -> OLIVEYOUNG
                else -> throw IllegalArgumentException()
            }
        }
    }
}