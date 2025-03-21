package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import domain.product.Store
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.error.CustomException
import org.team_alilm.error.ErrorCode
import org.team_alilm.gateway.CrawlingGateway
import org.team_alilm.util.CategoryUtil

@Service
class ZigzagProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val objectMapper: ObjectMapper
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(ZigzagProductCrawlingService::class.java)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        try {
            val response = crawlingGateway.htmlCrawling(request = CrawlingGateway.CrawlingGatewayRequest(url = command.url))
            val document = response.document
            val scriptTag = document.select("script#__NEXT_DATA__").firstOrNull()?.data() ?: ""
            val scriptJsonNode = objectMapper.readTree(scriptTag)

            val number = getNumber(scriptJsonNode)
            val (brand, name) = getProductBrandAndName(document.title())
            val (thumbnailUrl, imageList) = getThumbnailUrlAndImageList(document)
            val price = getPrice(document)
            val (firstCategory, secondCategory) = getCategories(scriptJsonNode)
            val (firstOptions, secondOptions, thirdOptions) = getOptions(scriptJsonNode)

            return ProductCrawlingUseCase.CrawlingResult(
                number = number,
                name = name,
                brand = brand,
                thumbnailUrl = thumbnailUrl,
                imageUrlList = imageList,
                firstCategory = firstCategory,
                secondCategory = secondCategory,
                price = price,
                store = Store.ZIGZAG,
                firstOptions = firstOptions,
                secondOptions = secondOptions,
                thirdOptions = thirdOptions
            )
        } catch (e: Exception) {
            log.error("Error occurred during crawling: ${e.message}", e)
            throw CustomException(ErrorCode.ZIGZAG_HTML_PARSING_ERROR)
        }
    }

    private fun getNumber(jsonNode: JsonNode): Long {
        return try {
            val props = jsonNode.get("props")
            val pageProps = props.get("pageProps")
            val product = pageProps.get("product")
            product.get("id").asLong()
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }

    private fun getProductBrandAndName(titleTag: String): Pair<String, String> {
        return try {
            val parts = titleTag.split(" ", limit = 2)
            val brand = parts.getOrNull(0) ?: ""
            val name = parts.getOrNull(1) ?: ""
            Pair(brand, name)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }

    private fun getThumbnailUrlAndImageList(document: Document): Pair<String, List<String>> {
        return try {
            val imageElements = document.select("img[alt='상품 이미지']")
            val firstImageUrl = imageElements.firstOrNull()?.attr("src")?.replace("webp", "jpeg") ?: ""
            val otherImageUrls = imageElements
                .drop(1)
                .map { it.attr("src").replace("webp", "jpeg") }
                .filter { it.isNotBlank() }
            Pair(firstImageUrl, otherImageUrls)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }

    private fun getPrice(document: Document): Int {
        return try {
            val priceElement = document.select("meta[property='product:price:amount']").firstOrNull()
            val priceText = priceElement?.attr("content") ?: throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
            priceText.replace(",", "").toInt()
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }

    private fun getCategories(jsonNode: JsonNode): Pair<String, String> {
        return try {
            val props = jsonNode.get("props")
            val pageProps = props.get("pageProps")
            val product = pageProps.get("product")
            val managedCategoryList = product.get("managed_category_list")

            Pair(
                CategoryUtil.getCategories(managedCategoryList),
                CategoryUtil.getCategories(managedCategoryList),
            )
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }

    private fun getOptions(jsonNode: JsonNode): Triple<List<String>, List<String>, List<String>> {
        return try {
            val props = jsonNode.get("props")
            val pageProps = props.get("pageProps")
            val product = pageProps.get("product")
            val options = product.get("product_item_attribute_list")

            val firstOptions = options[0]?.get("value_list")?.map { it.get("value").asText() } ?: emptyList()
            val secondOptions = options[1]?.get("value_list")?.map { it.get("value").asText() } ?: emptyList()
            val thirdOptions = options[2]?.get("value_list")?.map { it.get("value").asText() } ?: emptyList()

            Triple(firstOptions, secondOptions, thirdOptions)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.ZIGZAG_PRODUCT_NOT_FOUND)
        }
    }
}