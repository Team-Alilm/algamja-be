package org.team_alilm.global.util

enum class StringConstant(
    val string: String
) {

    MUSINSA_API_URL_TEMPLATE("https://goods-detail.musinsa.com/api2/goods/%s/options?goodsSaleType=SALE"),
    MUSINSA_PRODUCT_HTML_REQUEST_URL("https://store.musinsa.com/app/goods/%s");

    fun get(): String {
        return string
    }
}