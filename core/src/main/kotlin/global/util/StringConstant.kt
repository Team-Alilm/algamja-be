package org.team_alilm.global.util

enum class StringConstant(
    val value: String
) {

    MUSINSA_OPTION_API_URL("https://goods-detail.musinsa.com/api2/goods/%s/options?goodsSaleType=SALE"),
    MUSINSA_PRODUCT_HTML_URL("https://store.musinsa.com/app/goods/%s"),

    ABLY_ANONYMOUS_TOKEN("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhbm9ueW1vdXNfaWQiOiIzMTk0MzE3NjYiLCJpYXQiOjE3MzE4ODUwNjl9.iMCkiNw50N05BAatKxnvMYWAg_B7gBUiBL6FZe1Og9Y"),
    ABLY_PRODUCT_OPTIONS_API_URL("https://api.a-bly.com/api/v2/goods/%s/options/?depth=%s"),;
    fun get(): String {
        return value
    }
}