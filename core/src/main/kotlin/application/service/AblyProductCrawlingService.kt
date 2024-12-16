package org.team_alilm.application.service

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.domain.product.Store
import java.net.http.HttpHeaders

@Service
class AblyProductCrawlingService(
    private val restTemplate: RestTemplate,
    private val crawlingGateway: CrawlingGateway
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
//        val nextDataJson: String = fetchNextDataFromPage(command.url) ?: throw RuntimeException("Failed to fetch Next Data from page: ${command.url}")

        val productNumber = getProductNumber(command.url)
        val productDetailsUrl = getProductDetailsUrl(productNumber)

        val headers = org.springframework.http.HttpHeaders().apply {
            add("X-Anonymous-Token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhbm9ueW1vdXNfaWQiOiIzMTk0MzE3NjYiLCJpYXQiOjE3MzE4ODUwNjl9.iMCkiNw50N05BAatKxnvMYWAg_B7gBUiBL6FZe1Og9Y") // Authorization 헤더
        }

        val entity = HttpEntity<String>(headers) // HttpEntity에 헤더 추가

        val response = restTemplate.exchange(
            productDetailsUrl,  // 요청 URL
            HttpMethod.GET,     // HTTP 메서드
            entity,             // HttpEntity (헤더 포함)
            String::class.java  // 응답 타입
        )

        log.info("productDetails: ${response.body}") // 응답 내용 로깅

        return ProductCrawlingUseCase.CrawlingResult(
            number = 0L,
            name = "Example Name",
            brand = "Example Brand",
            thumbnailUrl = "https://example.com",
            firstCategory = "Example Category",
            secondCategory = "Example Subcategory",
            price = 0,
            store = Store.A_BLY,
            firstOptions = emptyList(),
            secondOptions = emptyList(),
            thirdOptions = emptyList()
        )
    }

    private fun fetchNextDataFromPage(url: String): String? {
        // WebDriver 초기화 (크롬 드라이버 사용)
        val options = ChromeOptions()
        options.addArguments("--headless")  // 브라우저를 표시하지 않고 실행

        val driver: WebDriver = ChromeDriver(options)

        try {
            // 웹 페이지 로드
            driver.get(url)

            // 페이지가 로드될 때까지 대기 (자세한 로딩 대기 방법을 추가할 수 있음)
            Thread.sleep(10000)

            log.info("driver.pageSource: " + driver.pageSource)

            // __NEXT_DATA__ 스크립트 가져오기
//            val nextDataScript = driver.findElement(By.id("__NEXT_DATA__"))
//            return nextDataScript?.getAttribute("innerHTML")
            return null
        } catch (e: Exception) {
            log.error("Error while fetching Next Data from page: $url", e)
            return null
        } finally {
            driver.quit()  // 드라이버 종료
        }
    }

    private fun getProductNumber(url: String): Long {
        return url.split("/").last().toLong()
    }

    private fun getProductDetailsUrl (productNumber: Long): String {
        return "https://api.a-bly.com/api/v2/goods/$productNumber"
    }

    private fun extractNextData(html: String): String? {
        // HTML 문서를 파싱
        val document = Jsoup.parse(html)

        // id가 '__NEXT_DATA__'인 <script> 태그를 찾음
        val scriptTag = document.getElementById("__NEXT_DATA__")

        // script 태그의 내용 (JSON)을 반환
        return scriptTag?.data()
    }
}