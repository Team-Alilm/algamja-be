package org.team_alilm.algamja.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option.OptionApiResponse

/**
 * 무신사 재고 API 테스트
 * remainQuantity가 0인 이유 분석
 */
fun main() {
    println("=== 무신사 재고 API 분석 ===\n")
    
    val restClient = RestClient.create()
    val objectMapper = ObjectMapper()
    
    // 테스트할 무신사 상품 번호들
    val testProducts = listOf(
        4652855L to "시티 레저 라이트웨이트 재킷", // 방금 등록한 상품
        3796989L to "테스트 상품 1",
        2974362L to "테스트 상품 2"
    )
    
    testProducts.forEach { (productNo, name) ->
        println("----------------------------------------")
        println("📦 상품: $name (상품번호: $productNo)")
        println("----------------------------------------")
        
        val uri = "https://goods-detail.musinsa.com/api2/goods/$productNo/v2/options?goodsSaleType=SALE"
        
        try {
            val response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(OptionApiResponse::class.java)
            
            if (response?.data?.optionItems != null) {
                println("✅ API 응답 성공")
                println("옵션 개수: ${response.data.optionItems.size}")
                
                response.data.optionItems.take(3).forEach { option ->
                    println("\n옵션 정보:")
                    println("  - 옵션명: ${option.optionValues.joinToString("/") { it.name }}")
                    println("  - activated: ${option.activated}")
                    println("  - outOfStock: ${option.outOfStock}")
                    println("  - isSoldOut: ${option.isSoldOut}")
                    println("  - isDeleted: ${option.isDeleted}")
                    println("  - remainQuantity: ${option.remainQuantity} ⚠️")
                    println("  - price: ${option.price}")
                    
                    // 재고 판단 로직
                    val hasStock = option.activated && 
                                  !option.outOfStock && 
                                  !option.isSoldOut && 
                                  !option.isDeleted
                    
                    val hasStockWithQuantity = hasStock && option.remainQuantity > 0
                    
                    println("\n  📊 재고 판단:")
                    println("    - remainQuantity 제외 시: ${if (hasStock) "✅ 재고 있음" else "❌ 품절"}")
                    println("    - remainQuantity 포함 시: ${if (hasStockWithQuantity) "✅ 재고 있음" else "❌ 품절"}")
                    
                    if (hasStock && !hasStockWithQuantity) {
                        println("    ⚠️ remainQuantity가 0이지만 다른 필드는 재고 있음을 나타냄")
                    }
                }
                
                // 전체 옵션 분석
                val totalOptions = response.data.optionItems.size
                val availableWithoutQuantity = response.data.optionItems.count { option ->
                    option.activated && !option.outOfStock && !option.isSoldOut && !option.isDeleted
                }
                val availableWithQuantity = response.data.optionItems.count { option ->
                    option.activated && !option.outOfStock && !option.isSoldOut && !option.isDeleted && option.remainQuantity > 0
                }
                val zeroQuantityButAvailable = response.data.optionItems.count { option ->
                    option.activated && !option.outOfStock && !option.isSoldOut && !option.isDeleted && option.remainQuantity == 0
                }
                
                println("\n📈 전체 통계:")
                println("  - 전체 옵션: $totalOptions개")
                println("  - remainQuantity 제외 재고 있음: $availableWithoutQuantity개")
                println("  - remainQuantity 포함 재고 있음: $availableWithQuantity개")
                println("  - remainQuantity=0 but 다른 필드는 재고있음: $zeroQuantityButAvailable개")
                
                // Raw JSON 출력 (첫 번째 옵션만)
                if (response.data.optionItems.isNotEmpty()) {
                    println("\n📄 Raw JSON (첫 번째 옵션):")
                    val firstOption = response.data.optionItems.first()
                    val json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firstOption)
                    println(json.take(500) + if (json.length > 500) "..." else "")
                }
                
            } else {
                println("❌ 옵션 데이터가 없습니다")
            }
            
        } catch (e: Exception) {
            println("❌ API 호출 실패: ${e.message}")
            e.printStackTrace()
        }
        
        println()
    }
    
    println("\n=== 분석 결과 ===")
    println("""
    remainQuantity가 0인 이유 가능성:
    1. 무신사가 실제 재고 수량을 공개하지 않는 정책
    2. remainQuantity는 특별한 용도(예약 재고, 한정 수량 등)로만 사용
    3. 실제 재고 판단은 activated, outOfStock, isSoldOut 필드로만 해야 함
    4. remainQuantity는 deprecated되었거나 내부용 필드일 가능성
    
    💡 권장사항:
    - remainQuantity 체크를 제거하고 다른 필드만으로 재고 판단
    - activated && !outOfStock && !isSoldOut && !isDeleted 조건만 사용
    """.trimIndent())
}