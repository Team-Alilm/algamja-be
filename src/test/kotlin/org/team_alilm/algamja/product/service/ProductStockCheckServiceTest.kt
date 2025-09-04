package org.team_alilm.algamja.product.service

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.product.entity.ProductRow
import java.math.BigDecimal

/**
 * 품절여부 체크 로직 테스트
 * 실제 무신사 API를 호출하여 동작 확인
 */
fun main() {
    println("=== 품절여부 체크 로직 테스트 시작 ===")
    
    val restClient = RestClient.create()
    val stockCheckService = ProductStockCheckService(restClient)
    
    // 테스트용 무신사 상품 데이터
    val testProducts = listOf(
        // 실제 무신사 상품번호와 옵션으로 테스트
        ProductRow(
            id = 1L,
            storeNumber = 3796989, // 무신사 실제 상품번호 예시
            name = "테스트 상품 1",
            brand = "테스트 브랜드",
            thumbnailUrl = "",
            store = Store.MUSINSA,
            price = BigDecimal.valueOf(39900),
            firstOption = "Black",
            secondOption = "M",
            thirdOption = null,
            firstCategory = "TOP",
            secondCategory = "티셔츠",
            isDelete = false,
            isNotification = false,
            isPurchase = false,
            isAvailable = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ),
        ProductRow(
            id = 2L,
            storeNumber = 2974362, // 다른 무신사 상품번호 예시
            name = "테스트 상품 2",
            brand = "테스트 브랜드",
            thumbnailUrl = "",
            store = Store.MUSINSA,
            price = BigDecimal.valueOf(59900),
            firstOption = "White",
            secondOption = "L",
            thirdOption = null,
            firstCategory = "OUTER",
            secondCategory = "자켓",
            isDelete = false,
            isNotification = false,
            isPurchase = false,
            isAvailable = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )
    
    // 각 상품에 대해 재고 확인
    testProducts.forEach { product ->
        try {
            println("\n--- 상품 테스트: ${product.name} (${product.storeNumber}) ---")
            println("옵션: ${product.firstOption}/${product.secondOption}/${product.thirdOption}")
            
            val startTime = System.currentTimeMillis()
            val isAvailable = stockCheckService.checkProductAvailability(product)
            val duration = System.currentTimeMillis() - startTime
            
            println("재고 상태: ${if (isAvailable) "✅ 재고 있음" else "❌ 품절"}")
            println("응답 시간: ${duration}ms")
            
        } catch (e: Exception) {
            println("❗ 에러 발생: ${e.message}")
            e.printStackTrace()
        }
    }
    
    // CM29, Zigzag 테스트
    val otherStores = listOf(
        ProductRow(
            id = 3L,
            storeNumber = 123456,
            name = "29CM 상품",
            brand = "브랜드",
            thumbnailUrl = "",
            store = Store.CM29,
            price = BigDecimal.valueOf(29900),
            firstOption = "",
            secondOption = null,
            thirdOption = null,
            firstCategory = "ETC",
            secondCategory = null,
            isDelete = false,
            isNotification = false,
            isPurchase = false,
            isAvailable = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ),
        ProductRow(
            id = 4L,
            storeNumber = 789012,
            name = "지그재그 상품",
            brand = "브랜드",
            thumbnailUrl = "",
            store = Store.ZIGZAG,
            price = BigDecimal.valueOf(19900),
            firstOption = "",
            secondOption = null,
            thirdOption = null,
            firstCategory = "ETC",
            secondCategory = null,
            isDelete = false,
            isNotification = false,
            isPurchase = false,
            isAvailable = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )
    
    println("\n\n=== 다른 스토어 테스트 ===")
    otherStores.forEach { product ->
        println("\n--- ${product.store} 테스트 ---")
        val isAvailable = stockCheckService.checkProductAvailability(product)
        println("결과: ${if (isAvailable) "재고 있음" else "미구현/품절"}")
    }
    
    println("\n=== 테스트 완료 ===")
}