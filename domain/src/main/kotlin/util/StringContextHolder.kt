package util

enum class StringContextHolder(
    val value: String
) {

    MUSINSA_OPTION_API_URL("https://goods-detail.musinsa.com/api2/goods/%s/options?goodsSaleType=SALE"),
    MUSINSA_PRODUCT_HTML_URL("https://store.musinsa.com/app/goods/%s"),
    MUSINSA_PRODUCT_IMAGES_URL("https://goods-detail.musinsa.com/api2/goods/%s/recommends/multi?uuid=detail_goods_attributes_allbrand&limit=3"),
    MUSINSA_PRODUCT_URL("https://www.musinsa.com/products/%s"),

    ABLY_PRODUCT_API_URL("https://api.a-bly.com/api/v2/goods/%s/?channel=0"),
    ABLY_ANONYMOUS_TOKEN_API_URL("https://api.a-bly.com/api/v2/anonymous/token/"),
    ABLY_PRODUCT_OPTIONS_API_URL("https://api.a-bly.com/api/v2/goods/%s/options/?depth=%s"),

    CM29_PRODUCT_DETAIL_API_URL("https://bff-api.29cm.co.kr/api/v5/product-detail/%s"),
    CM29_PRODUCT_URL("https://product.29cm.co.kr/catalog/%s"),

    ZIGZAG_PRODUCT_SOLD_API_URL("https://api.zigzag.kr/api/2/graphql/GetCatalogProductDetailPageOption"),
    ZIGZAG_PRODUCT_URL("https://zigzag.kr/catalog/products/%s"),

    LOCAL_HOST("https://alilm.co.kr/product/%s");

    fun get(): String {
        return value
    }
}