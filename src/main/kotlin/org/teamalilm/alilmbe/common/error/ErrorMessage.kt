package org.teamalilm.alilmbe.common.error

enum class ErrorMessage(
    val code: String,
    val message: String
) {
    PRODUCT_ALREADY_EXISTS("ALILM-001", "Product already exists"),
    BASKET_ALREADY_EXISTS("ALILM-002", "Basket already exists"),
}