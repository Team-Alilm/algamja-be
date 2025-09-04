package org.team_alilm.algamja.product.crawler.impl.ably

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.enums.ProductCategory
import java.math.BigDecimal
import java.net.URI
import java.time.Duration
import java.util.regex.Pattern
import kotlin.random.Random

@Component("ablySeleniumCrawler")
@Order(20) // 가장 낮은 우선순위
class AblySeleniumCrawler : ProductCrawler {

    companion object {
        private const val DEFAULT_BRAND = "Unknown"
        private const val WAIT_TIMEOUT_SECONDS = 30L
        private const val PAGE_LOAD_DELAY_MS = 3000L
        private const val ELEMENT_LOAD_DELAY_MS = 1000L
        
        private val REAL_USER_AGENTS = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        )
    }

    private val log = LoggerFactory.getLogger(javaClass)
    private val goodsUrlPattern = Pattern.compile(""".*/goods/(\d+).*""")
    
    override fun supports(url: String): Boolean {
        return runCatching {
            val uri = URI(url.trim())
            val host = uri.host?.lowercase()
            val isSupported = (host == "a-bly.com" || host == "m.a-bly.com" || host?.endsWith(".a-bly.com") == true) &&
                    goodsUrlPattern.matcher(url).matches()
            
            log.trace("Selenium URL support check: url={}, host={}, supported={}", url, host, isSupported)
            isSupported
        }.getOrElse { 
            log.debug("Failed to parse URL for support check: {}", url)
            false 
        }
    }
    
    override fun normalize(url: String): String {
        return runCatching {
            val uri = URI(url.trim())
            val scheme = (uri.scheme ?: "https").lowercase()
            val host = uri.host?.lowercase()?.let {
                if (it == "a-bly.com") "m.a-bly.com" else it // 모바일 버전 사용
            } ?: return url.substringBefore("?")
            
            val path = uri.path ?: ""
            val normalizedUrl = "$scheme://$host$path".substringBefore("?")
            
            log.debug("Selenium URL normalization: {} -> {}", url, normalizedUrl)
            normalizedUrl
        }.getOrElse { 
            val fallback = url.substringBefore("?")
            log.debug("Failed to normalize URL, using fallback: {} -> {}", url, fallback)
            fallback
        }
    }
    
    override fun fetch(url: String): CrawledProduct {
        val startTime = System.currentTimeMillis()
        val goodsId = extractGoodsId(url)
        val normalizedUrl = normalize(url)
        
        log.info("Starting Selenium-based Ably crawling for goodsId: {}, url: {}", goodsId, normalizedUrl)
        
        val driver = createWebDriver()
        
        try {
            setupCloudflareBypass(driver)
            
            driver.get(normalizedUrl)
            Thread.sleep(PAGE_LOAD_DELAY_MS)
            
            val wait = WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS))
            
            // 상품명 대기 및 추출
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-title, .goods-title, h1")))
            Thread.sleep(ELEMENT_LOAD_DELAY_MS)
            
            val productName = extractProductName(driver)
            val brand = extractBrand(driver)
            val price = extractPrice(driver)
            val images = extractImages(driver)
            val category = extractCategory(driver)
            val options = extractOptions(driver)
            
            val categories = mapCategories(category)
            
            val crawledProduct = CrawledProduct(
                storeNumber = goodsId,
                name = productName,
                brand = brand,
                thumbnailUrl = images.firstOrNull() ?: "",
                imageUrls = images,
                store = Store.ABLY,
                price = price,
                firstCategory = categories.first,
                secondCategory = categories.second,
                firstOptions = options.first,
                secondOptions = options.second,
                thirdOptions = options.third
            )
            
            val duration = System.currentTimeMillis() - startTime
            log.info("Successfully crawled Ably product with Selenium - goodsId: {} in {}ms", goodsId, duration)
            
            return crawledProduct
            
        } catch (e: Exception) {
            log.error("Selenium crawling failed for goodsId: {}, url: {}", goodsId, normalizedUrl, e)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        } finally {
            runCatching { driver.quit() }
        }
    }
    
    private fun createWebDriver(): WebDriver {
        val userAgent = REAL_USER_AGENTS[Random.nextInt(REAL_USER_AGENTS.size)]
        
        val options = ChromeOptions().apply {
            // Cloudflare 우회를 위한 설정
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage") 
            addArguments("--disable-gpu")
            addArguments("--disable-extensions")
            addArguments("--disable-plugins")
            addArguments("--disable-images") // 이미지 로딩 비활성화로 속도 향상
            addArguments("--disable-javascript") // 불필요한 JS 비활성화
            addArguments("--user-agent=$userAgent")
            
            // Cloudflare 탐지 우회 설정
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("--disable-features=VizDisplayCompositor")
            setExperimentalOption("excludeSwitches", listOf("enable-automation"))
            setExperimentalOption("useAutomationExtension", false)
            
            // 헤드리스 모드는 EC2 환경에서만 사용 (로컬에서는 GUI 모드로 테스트 가능)
            val isEC2 = System.getenv("AWS_EXECUTION_ENV") != null || System.getProperty("os.name")?.contains("Linux") == true
            if (isEC2) {
                addArguments("--headless=new") // 새로운 헤드리스 모드 사용
                addArguments("--window-size=1920,1080")
                addArguments("--display=:99") // Xvfb 디스플레이 사용 (필요시)
            }
            
            log.debug("Chrome options configured with User-Agent: {}, Headless: {}", userAgent, isEC2)
        }
        
        return ChromeDriver(options)
    }
    
    private fun setupCloudflareBypass(driver: WebDriver) {
        try {
            // 자바스크립트를 통한 navigator 속성 숨기기
            if (driver is ChromeDriver) {
                driver.executeScript("""
                    Object.defineProperty(navigator, 'webdriver', {
                        get: () => undefined
                    });
                """)
                
                driver.executeScript("""
                    Object.defineProperty(navigator, 'plugins', {
                        get: () => [1, 2, 3, 4, 5]
                    });
                """)
                
                log.debug("JavaScript-based Cloudflare bypass configured")
            }
        } catch (e: Exception) {
            log.warn("Failed to setup Cloudflare bypass: {}", e.message)
        }
    }
    
    private fun extractProductName(driver: WebDriver): String {
        val selectors = listOf(
            ".product-title",
            ".goods-title", 
            ".product-info .title",
            "h1",
            ".product-name",
            "[class*='title']"
        )
        
        for (selector in selectors) {
            try {
                val element = driver.findElement(By.cssSelector(selector))
                val name = element.text.trim()
                if (name.isNotEmpty()) {
                    log.debug("Product name found with selector '{}': {}", selector, name)
                    return name
                }
            } catch (_: Exception) { }
        }
        
        log.warn("Failed to extract product name, using page title")
        return driver.title?.substringBefore(" - ")?.trim()?.takeIf { it.isNotEmpty() } ?: "Unknown Product"
    }
    
    private fun extractBrand(driver: WebDriver): String {
        val selectors = listOf(
            ".brand-name",
            ".brand",
            ".market-name",
            ".seller-name",
            "[class*='brand']"
        )
        
        for (selector in selectors) {
            try {
                val element = driver.findElement(By.cssSelector(selector))
                val brand = element.text.trim()
                if (brand.isNotEmpty()) {
                    log.debug("Brand found with selector '{}': {}", selector, brand)
                    return brand
                }
            } catch (_: Exception) { }
        }
        
        log.debug("Brand not found, using default")
        return DEFAULT_BRAND
    }
    
    private fun extractPrice(driver: WebDriver): BigDecimal {
        val selectors = listOf(
            ".price-sale",
            ".price",
            ".product-price",
            "[class*='price']",
            ".cost"
        )
        
        for (selector in selectors) {
            try {
                val element = driver.findElement(By.cssSelector(selector))
                val priceText = element.text.trim()
                val priceNumber = priceText.replace(Regex("[^0-9]"), "")
                if (priceNumber.isNotEmpty()) {
                    val price = BigDecimal(priceNumber)
                    log.debug("Price found with selector '{}': {}", selector, price)
                    return price
                }
            } catch (_: Exception) { }
        }
        
        log.warn("Price not found, using 0")
        return BigDecimal.ZERO
    }
    
    private fun extractImages(driver: WebDriver): List<String> {
        val images = mutableSetOf<String>()
        val selectors = listOf(
            ".product-images img",
            ".goods-images img", 
            ".product-gallery img",
            ".swiper-slide img",
            ".image-gallery img"
        )
        
        for (selector in selectors) {
            try {
                val elements = driver.findElements(By.cssSelector(selector))
                elements.forEach { element ->
                    val src = element.getAttribute("src") ?: element.getAttribute("data-src")
                    if (!src.isNullOrEmpty() && src.startsWith("http")) {
                        images.add(src)
                    }
                }
            } catch (_: Exception) { }
        }
        
        log.debug("Extracted {} product images", images.size)
        return images.toList()
    }
    
    private fun extractCategory(driver: WebDriver): String {
        val selectors = listOf(
            ".breadcrumb a",
            ".category",
            ".product-category",
            "[class*='category']"
        )
        
        for (selector in selectors) {
            try {
                val elements = driver.findElements(By.cssSelector(selector))
                val categories = elements.mapNotNull { it.text.trim().takeIf { text -> text.isNotEmpty() } }
                if (categories.isNotEmpty()) {
                    val category = categories.last() // 마지막 카테고리가 가장 구체적
                    log.debug("Category found with selector '{}': {}", selector, category)
                    return category
                }
            } catch (_: Exception) { }
        }
        
        log.debug("Category not found")
        return ""
    }
    
    private fun extractOptions(driver: WebDriver): Triple<List<String>, List<String>, List<String>> {
        val firstOptions = mutableSetOf<String>()
        val secondOptions = mutableSetOf<String>()
        val thirdOptions = mutableSetOf<String>()
        
        val optionSelectors = listOf(
            ".option-select option",
            ".option-list .option",
            ".product-option option",
            "[class*='option'] option"
        )
        
        try {
            for (selector in optionSelectors) {
                val elements = driver.findElements(By.cssSelector(selector))
                elements.forEach { element ->
                    val optionText = element.text.trim()
                    if (optionText.isNotEmpty() && !optionText.contains("선택") && !optionText.contains("옵션")) {
                        when {
                            firstOptions.size < 10 -> firstOptions.add(optionText)
                            secondOptions.size < 10 -> secondOptions.add(optionText)
                            else -> thirdOptions.add(optionText)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.debug("Failed to extract options: {}", e.message)
        }
        
        log.debug("Extracted options - first: {}, second: {}, third: {}", 
                 firstOptions.size, secondOptions.size, thirdOptions.size)
        
        return Triple(firstOptions.toList(), secondOptions.toList(), thirdOptions.toList())
    }
    
    private fun extractGoodsId(url: String): Long {
        val matcher = goodsUrlPattern.matcher(url)
        return if (matcher.matches()) {
            val goodsId = matcher.group(1).toLong()
            log.debug("Extracted goodsId: {} from URL: {}", goodsId, url)
            goodsId
        } else {
            log.error("Failed to extract goodsId from URL: {}", url)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_URL)
        }
    }
    
    private fun mapCategories(categoryName: String): Pair<String, String?> {
        val koreanCategory = CategoryMapper.mapCategory(categoryName)
        val englishFirstCategory = ProductCategory.mapKoreanToEnglish(koreanCategory) ?: "OTHERS"
        val englishSecondCategory = ProductCategory.mapKoreanToEnglish(categoryName)
        return Pair(englishFirstCategory, englishSecondCategory)
    }
}