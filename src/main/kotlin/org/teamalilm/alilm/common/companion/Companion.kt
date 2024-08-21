package org.teamalilm.alilm.common.companion

enum class StringConstant(
    val string: String
) {

    MUSINSA_API_URL_TEMPLATE("https://goods-detail.musinsa.com/api2/goods/%s/options?goodsSaleType=SALE");

    fun get(): String {
        return string
    }
}