package org.team_alilm.algamja.product.crawler.impl.zigzag

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.zigzag.dto.ZigzagApiResponse
import org.team_alilm.algamja.product.crawler.impl.zigzag.dto.ZigzagGraphQLRequest
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import java.net.IDN
import java.net.URI

@Component
class ZigzagCrawler(
    private val restClient: RestClient,
) : ProductCrawler {

    private val API_URL = "https://api.zigzag.kr/api/2/graphql/GetCatalogProductDetailPageOption"
    
    private val graphQLQuery = """
        fragment OptionItemList on PdpCatalogItem { id name price price_delta final_price item_code sales_status display_status remain_stock is_zigzin delivery_type expected_delivery_date expected_delivery_time discount_info { image_url title color order } item_attribute_list { id name value value_id } wms_notification_info { active } } query GetCatalogProductDetailPageOption(${'$'}catalog_product_id: ID!, ${'$'}input: PdpBaseInfoInput) { pdp_option_info(catalog_product_id: ${'$'}catalog_product_id, input: ${'$'}input) { catalog_product { shop_id shop_name shop_main_domain id name fulfillment_type external_code minimum_order_quantity maximum_order_quantity coupon_available_status scheduled_sale_date discount_info { image_url title color } estimated_shipping_date { estimate_list { day probability } } shipping_fee { fee_type base_fee minimum_free_shipping_fee } shipping_company { return_company } managed_category_list { id category_id value key depth } meta_catalog_product_info { id is_able_to_buy pdp_url browsing_type } product_price { first_order_discount { price promotion_id discount_type discount_amount discount_rate_bp min_required_amount } coupon_discount_info_list { target_type discount_type discount_amount discount_rate_bp discount_amount_of_amount_coupon min_required_amount max_discount_amount } display_final_price { final_price { badge { text color { normal } } color { normal } } final_price_additional { badge { text } color { normal } } } product_promotion_discount_info { discount_amount } max_price_info { price } final_discount_info { discount_price } } trait_list { type } promotion_info { bogo_required_quantity promotion_id promotion_type bogo_info { required_quantity discount_type discount_amount discount_rate_bp } } product_image_list { url origin_url pdp_thumbnail_url pdp_static_image_url image_type } product_option_list { id order name code required option_type value_list { id code value static_url jpeg_url } } matching_catalog_product_info { id name is_able_to_buy pdp_url fulfillment_type browsing_type external_code product_price { max_price_info { price color { normal } badge { text color { normal } } } final_discount_info { discount_price } } discount_info { color title image_url order } shipping_fee { fee_type base_fee minimum_free_shipping_fee } option_list { id order name code required option_type value_list { id code value static_url jpeg_url } } } product_additional_option_list { id order name code required option_type value_list { id code value static_url jpeg_url } } additional_item_list { id name price price_delta item_code sales_status display_status option_type is_zigzin delivery_type expected_delivery_date item_attribute_list { id name value value_id } wms_notification_info { active } } custom_input_option_list { name is_required: required max_length } matched_item_list { ...OptionItemList } zigzin_item_list { ...OptionItemList } is_only_zigzin_button_visible color_image_list { is_main image_url image_width image_height webp_image_url color_list } store_deal_banner { title guide_text } category_list { category_id value } shipping_fee { fee_type base_fee minimum_free_shipping_fee additional_shipping_fee_text } minimum_order_quantity_type } flags { is_purchase_only_one_at_time is_cart_button_visible } key_color { buy_button { text { disabled enabled } background { disabled enabled } } discount_info_of_atf } } }
    """.trimIndent()

    override fun supports(url: String): Boolean =
        runCatching {
            val u = URI(url.trim())
            val scheme = u.scheme?.lowercase()
            val host = normalizeHost(u.host) ?: return false
            (scheme == "http" || scheme == "https") &&
                    (host == "zigzag.kr" || host.endsWith(".zigzag.kr"))
        }.getOrDefault(false)

    override fun normalize(url: String): String =
        runCatching {
            val u = URI(url.trim())
            val scheme = (u.scheme ?: "https").lowercase()
            val host = normalizeHost(u.host) ?: return url.substringBefore("?")
            val path = u.rawPath.orEmpty()
            if ((scheme == "http" || scheme == "https") &&
                (host == "zigzag.kr" || host.endsWith(".zigzag.kr"))
            ) {
                val port = when (u.port) {
                    -1, 80, 443 -> ""
                    else -> ":${u.port}"
                }
                "$scheme://$host$port$path"
            } else url.substringBefore("?")
        }.getOrElse { url.substringBefore("?") }

    private fun normalizeHost(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim().trimEnd('.').lowercase()
        return IDN.toASCII(trimmed)
    }

    override fun fetch(url: String): CrawledProduct {
        val normalized = normalize(url)
        val catalogProductId = extractCatalogProductId(normalized)

        val request = ZigzagGraphQLRequest(
            query = graphQLQuery,
            variables = mapOf(
                "catalog_product_id" to catalogProductId,
                "input" to mapOf(
                    "catalog_product_id" to catalogProductId,
                    "entry_source_type" to ""
                )
            )
        )

        val response = try {
            restClient.post()
                .uri(API_URL)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0 (compatible; AlilmBot/1.0)")
                .body(request)
                .retrieve()
                .body(ZigzagApiResponse::class.java)
                ?: throw BusinessException(ErrorCode.ZIGZAG_INVALID_RESPONSE)
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.ZIGZAG_INVALID_RESPONSE)
        }

        val catalogProduct = response.data.pdpOptionInfo.catalogProduct

        val imageUrls = catalogProduct.productImageList
            .map { it.url }
            .distinct()

        val thumbnailUrl = catalogProduct.productImageList
            .find { it.imageType == "MAIN" }?.url
            ?: catalogProduct.productImageList.firstOrNull()?.url
            ?: ""

        val maxPrice = catalogProduct.productPrice.maxPriceInfo.price
        val discountPrice = catalogProduct.productPrice.finalDiscountInfo?.discountPrice
        val finalPrice = discountPrice ?: maxPrice

        val categories = catalogProduct.managedCategoryList.sortedBy { it.depth }
        val allCategoryNames = categories.joinToString(" ") { it.value }
        val firstCategory = CategoryMapper.mapCategory(allCategoryNames)
        val secondCategory = categories.find { it.depth == 2 }?.value

        val firstOptions = catalogProduct.productOptionList.find { it.order == 0 }
            ?.valueList?.map { it.value } ?: emptyList()
        val secondOptions = catalogProduct.productOptionList.find { it.order == 1 }
            ?.valueList?.map { it.value } ?: emptyList()
        val thirdOptions = catalogProduct.productOptionList.find { it.order == 2 }
            ?.valueList?.map { it.value } ?: emptyList()

        return CrawledProduct(
            storeNumber = runCatching { catalogProduct.id.toLong() }.getOrElse { 0L },
            name = catalogProduct.name.takeIf { it.isNotBlank() } ?: "상품명 없음",
            brand = catalogProduct.shopName.takeIf { it.isNotBlank() } ?: "브랜드 없음",
            thumbnailUrl = thumbnailUrl,
            imageUrls = imageUrls,
            store = "ZIGZAG",
            price = finalPrice.toBigDecimal(),
            firstCategory = firstCategory,
            secondCategory = secondCategory,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
    }

    private fun extractCatalogProductId(url: String): String {
        val regex = Regex("/catalog/products/(\\d+)")
        val matchResult = regex.find(url)
            ?: throw BusinessException(ErrorCode.ZIGZAG_INVALID_RESPONSE)
        return matchResult.groupValues[1]
    }
}