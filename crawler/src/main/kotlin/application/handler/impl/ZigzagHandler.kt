package org.team_alilm.application.handler.impl

import com.fasterxml.jackson.databind.JsonNode
import domain.product.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.application.handler.PlatformHandler
import util.StringContextHolder

@Component
class ZigzagHandler(
    private val restClient: RestClient
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        val responseUrl = StringContextHolder.ZIGZAG_PRODUCT_SOLD_API_URL.get()

        // GraphQL 쿼리
        val query = """
        query GetCatalogProductDetailPageOption(\${product.number}: ID!) {
            pdp_option_info(catalog_product_id: \${product.number}) {
                catalog_product {
                    id
                    fulfillment_type
                    shop_main_domain
                    external_code
                    minimum_order_quantity
                    maximum_order_quantity
                    coupon_available_status
                    promotion_info {
                        bogo_required_quantity
                        promotion_id
                        promotion_type
                        bogo_info {
                            required_quantity
                            discount_type
                            discount_amount
                            discount_rate_bp
                        }
                    }
                    product_image_list {
                        url
                        origin_url
                        pdp_thumbnail_url
                        pdp_static_image_url
                        image_type
                    }
                    product_option_list {
                        id
                        order
                        name
                        code
                        required
                        option_type
                        value_list {
                            id
                            code
                            value
                            static_url
                            jpeg_url
                        }
                    }
                }
            }
        }
    """.trimIndent()

        // GraphQL 요청 본문 생성
        val requestBody = """
        {
            "query": "$query",
            "variables": {
                "catalog_product_id": "${product.number}"
            }
        }
    """.trimIndent()

        // REST 클라이언트를 사용해 POST 요청 보내기
        val response = restClient.post()
            .uri(responseUrl)
            .header("Content-Type", "application/json")
            .body(requestBody) // GraphQL 쿼리와 변수를 body에 포함
            .retrieve() // 응답을 가져옴
            .body(JsonNode::class.java) // 응답을 문자열로 변환

        log.info("Response: $response")

        // 응답 처리
        return true
    }

}