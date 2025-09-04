package org.team_alilm.algamja.product.crawler.impl.ably

import org.springframework.web.client.RestClient

/**
 * Ably 크롤러 수동 테스트 클래스
 * EC2 환경에서의 동작을 확인하기 위한 간단한 테스트
 */
fun main() {
    println("🚀 Ably Crawler Manual Test Started")
    
    val restClient = RestClient.create()
    val ablyCrawler = AblyCrawler(restClient)
    
    val testUrl = "https://m.a-bly.com/goods/27966224"
    
    try {
        println("📱 Testing URL: $testUrl")
        println("✅ URL Support Check: ${ablyCrawler.supports(testUrl)}")
        println("🔗 Normalized URL: ${ablyCrawler.normalize(testUrl)}")
        
        println("\n🔍 Starting API-based crawling...")
        val startTime = System.currentTimeMillis()
        
        val result = ablyCrawler.fetch(testUrl)
        
        val duration = System.currentTimeMillis() - startTime
        
        println("✅ Crawling completed in ${duration}ms")
        println("📦 Product Details:")
        println("  - Name: ${result.name}")
        println("  - Brand: ${result.brand}")
        println("  - Price: ${result.price}")
        println("  - Store: ${result.store}")
        println("  - Images: ${result.imageUrls.size}")
        println("  - Options: ${result.firstOptions.size}/${result.secondOptions.size}/${result.thirdOptions.size}")
        
        if (result.firstOptions.isNotEmpty()) {
            println("  - First Options: ${result.firstOptions.take(3).joinToString(", ")}${if (result.firstOptions.size > 3) "..." else ""}")
        }
        
    } catch (e: Exception) {
        println("❌ API Crawling failed: ${e.message}")
        e.printStackTrace()
        
        // Selenium 크롤러 테스트는 실제 Chrome이 설치된 환경에서만 가능
        println("\n💡 For Selenium testing, ensure Chrome is installed and run on appropriate environment")
    }
    
    println("\n✨ Test completed!")
}