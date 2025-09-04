package org.team_alilm.algamja.product.crawler.impl.ably

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
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

@Component("ablySeleniumEnhanced")
@Order(15)
class AblySeleniumEnhanced : ProductCrawler {
    
    companion object {
        private const val DEFAULT_BRAND = "Unknown"
        private const val WAIT_TIMEOUT_SECONDS = 30L
        private const val PAGE_LOAD_DELAY_MS = 2000L
        private const val SCROLL_DELAY_MS = 500L
        
        // 모바일 User-Agent
        private val MOBILE_USER_AGENTS = listOf(
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        )
        
        // 상품 정보 셀렉터 (우선순위 순)
        private val PRODUCT_NAME_SELECTORS = listOf(
            "h1.goods-title",
            ".product-title",
            ".goods-info__title",
            "[class*='title'][class*='goods']",
            "h1"
        )
        
        private val BRAND_SELECTORS = listOf(
            ".market-name",
            ".brand-name",
            ".seller-name",
            "[class*='market']",
            "[class*='brand']"
        )
        
        private val PRICE_SELECTORS = listOf(
            ".price-sale",
            ".sale-price",
            ".price",
            "[class*='price'][class*='sale']",
            "[class*='price']"
        )
        
        private val OPTION_SELECTORS = listOf(
            ".option-select",
            ".goods-option",
            "[class*='option'][class*='select']",
            "select[name*='option']"
        )
    }
    
    private val log = LoggerFactory.getLogger(javaClass)
    private val goodsUrlPattern = Pattern.compile(""".*/goods/(\d+).*""")
    
    override fun supports(url: String): Boolean {
        return runCatching {
            val uri = URI(url.trim())
            val host = uri.host?.lowercase()
            (host == "a-bly.com" || host == "m.a-bly.com" || host?.endsWith(".a-bly.com") == true) &&
                goodsUrlPattern.matcher(url).matches()
        }.getOrElse { false }
    }
    
    override fun normalize(url: String): String {
        return runCatching {
            val uri = URI(url.trim())
            val scheme = (uri.scheme ?: "https").lowercase()
            // 모바일 버전 사용
            val host = "m.a-bly.com"
            val path = uri.path ?: ""
            "$scheme://$host$path".substringBefore("?")
        }.getOrElse { url.substringBefore("?") }
    }
    
    override fun fetch(url: String): CrawledProduct {
        val startTime = System.currentTimeMillis()
        val goodsId = extractGoodsId(url)
        val mobileUrl = normalize(url)
        
        log.info("Starting enhanced Selenium crawling for goodsId: {}, url: {}", goodsId, mobileUrl)
        
        val driver = createMobileWebDriver()
        
        try {
            // 페이지 로드
            driver.get(mobileUrl)
            
            // JavaScript로 봇 탐지 우회
            bypassBotDetection(driver as JavascriptExecutor)
            
            // 페이지 로드 대기
            val wait = WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS))
            
            // 상품명이 로드될 때까지 명시적 대기
            val productNameElement = waitForElement(wait, PRODUCT_NAME_SELECTORS)
            
            // 스크롤로 lazy loading 트리거
            scrollToLoadContent(driver as JavascriptExecutor)
            
            // 상품 정보 추출
            val productName = extractText(driver, PRODUCT_NAME_SELECTORS) ?: "Unknown Product"
            val brand = extractText(driver, BRAND_SELECTORS) ?: DEFAULT_BRAND
            val price = extractPrice(driver)
            val images = extractImages(driver)
            val options = extractOptions(driver, wait)
            val category = extractCategory(driver)
            
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
            log.info("Successfully crawled with enhanced Selenium - goodsId: {} in {}ms", goodsId, duration)
            
            return crawledProduct
            
        } catch (e: Exception) {
            log.error("Enhanced Selenium crawling failed for goodsId: {}", goodsId, e)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        } finally {
            runCatching { driver.quit() }
        }
    }
    
    private fun createMobileWebDriver(): WebDriver {
        val userAgent = MOBILE_USER_AGENTS[Random.nextInt(MOBILE_USER_AGENTS.size)]
        
        val options = ChromeOptions().apply {
            // 기본 설정
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-gpu")
            addArguments("--disable-web-security")
            addArguments("--disable-features=VizDisplayCompositor")
            addArguments("--disable-blink-features=AutomationControlled")
            
            // 모바일 설정
            addArguments("--user-agent=$userAgent")
            addArguments("--window-size=390,844") // iPhone 14 Pro 크기
            addArguments("--lang=ko-KR")
            
            // 실험적 옵션
            setExperimentalOption("excludeSwitches", listOf("enable-automation"))
            setExperimentalOption("useAutomationExtension", false)
            
            // 모바일 에뮬레이션
            val mobileEmulation = mapOf(
                "deviceMetrics" to mapOf(
                    "width" to 390,
                    "height" to 844,
                    "pixelRatio" to 3.0
                ),
                "userAgent" to userAgent
            )
            setExperimentalOption("mobileEmulation", mobileEmulation)
            
            // 프리퍼런스
            val prefs = mapOf(
                "credentials_enable_service" to false,
                "profile.password_manager_enabled" to false,
                "profile.default_content_setting_values.notifications" to 2
            )
            setExperimentalOption("prefs", prefs)
            
            // EC2/헤드리스 환경 감지
            val isHeadless = System.getenv("AWS_EXECUTION_ENV") != null || 
                           System.getProperty("os.name")?.contains("Linux") == true
            if (isHeadless) {
                addArguments("--headless=new")
                addArguments("--display=:99")
            }
        }
        
        return ChromeDriver(options)
    }
    
    private fun bypassBotDetection(js: JavascriptExecutor) {
        // Navigator.webdriver 숨기기
        js.executeScript("""
            Object.defineProperty(navigator, 'webdriver', {
                get: () => undefined
            });
        """)
        
        // Chrome 관련 속성 추가
        js.executeScript("""
            window.chrome = {
                runtime: {}
            };
        """)
        
        // Permission API 모킹
        js.executeScript("""
            const originalQuery = window.navigator.permissions.query;
            window.navigator.permissions.query = (parameters) => (
                parameters.name === 'notifications' ?
                    Promise.resolve({ state: Notification.permission }) :
                    originalQuery(parameters)
            );
        """)
        
        // Plugin 배열 설정
        js.executeScript("""
            Object.defineProperty(navigator, 'plugins', {
                get: () => [1, 2, 3, 4, 5]
            });
        """)
        
        // Language 설정
        js.executeScript("""
            Object.defineProperty(navigator, 'language', {
                get: () => 'ko-KR'
            });
            Object.defineProperty(navigator, 'languages', {
                get: () => ['ko-KR', 'ko', 'en-US', 'en']
            });
        """)
    }
    
    private fun waitForElement(wait: WebDriverWait, selectors: List<String>): WebElement? {
        for (selector in selectors) {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)))
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }
    
    private fun scrollToLoadContent(js: JavascriptExecutor) {
        // 점진적 스크롤로 lazy loading 트리거
        val scrollSteps = listOf(300, 600, 900, 1200)
        for (position in scrollSteps) {
            js.executeScript("window.scrollTo(0, $position);")
            Thread.sleep(SCROLL_DELAY_MS)
        }
        // 맨 위로 복귀
        js.executeScript("window.scrollTo(0, 0);")
    }
    
    private fun extractText(driver: WebDriver, selectors: List<String>): String? {
        for (selector in selectors) {
            try {
                val element = driver.findElement(By.cssSelector(selector))
                val text = element.text.trim()
                if (text.isNotEmpty()) {
                    log.debug("Found text with selector '{}': {}", selector, text)
                    return text
                }
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }
    
    private fun extractPrice(driver: WebDriver): BigDecimal {
        val priceText = extractText(driver, PRICE_SELECTORS)
        if (priceText != null) {
            val priceNumber = priceText.replace(Regex("[^0-9]"), "")
            if (priceNumber.isNotEmpty()) {
                return BigDecimal(priceNumber)
            }
        }
        return BigDecimal.ZERO
    }
    
    private fun extractImages(driver: WebDriver): List<String> {
        val images = mutableSetOf<String>()
        val selectors = listOf(
            ".swiper-slide img",
            ".product-images img",
            ".goods-images img",
            "[class*='swiper'] img",
            "img[src*='goods']"
        )
        
        for (selector in selectors) {
            try {
                val elements = driver.findElements(By.cssSelector(selector))
                elements.forEach { element ->
                    val src = element.getAttribute("src") 
                        ?: element.getAttribute("data-src")
                        ?: element.getAttribute("data-lazy-src")
                    
                    if (!src.isNullOrEmpty() && src.startsWith("http")) {
                        images.add(src)
                    }
                }
            } catch (_: Exception) {
                continue
            }
        }
        
        log.debug("Extracted {} product images", images.size)
        return images.toList()
    }
    
    private fun extractOptions(driver: WebDriver, wait: WebDriverWait): Triple<List<String>, List<String>, List<String>> {
        val firstOptions = mutableSetOf<String>()
        val secondOptions = mutableSetOf<String>()
        val thirdOptions = mutableSetOf<String>()
        
        for (selector in OPTION_SELECTORS) {
            try {
                // 옵션 선택 요소 찾기
                val optionElements = driver.findElements(By.cssSelector(selector))
                
                optionElements.forEach { element ->
                    // SELECT 요소인 경우
                    if (element.tagName.lowercase() == "select") {
                        val options = element.findElements(By.tagName("option"))
                        options.forEach { option ->
                            val text = option.text.trim()
                            if (text.isNotEmpty() && !text.contains("선택")) {
                                when {
                                    firstOptions.size < 20 -> firstOptions.add(text)
                                    secondOptions.size < 20 -> secondOptions.add(text)
                                    else -> thirdOptions.add(text)
                                }
                            }
                        }
                    }
                    // 클릭 가능한 옵션 버튼인 경우
                    else {
                        try {
                            element.click()
                            Thread.sleep(500)
                            
                            // 옵션 팝업/드롭다운 대기
                            val optionListSelectors = listOf(
                                ".option-list",
                                ".option-popup",
                                "[class*='option'][class*='list']"
                            )
                            
                            for (listSelector in optionListSelectors) {
                                try {
                                    val optionList = driver.findElements(By.cssSelector("$listSelector li, $listSelector button"))
                                    optionList.forEach { item ->
                                        val text = item.text.trim()
                                        if (text.isNotEmpty()) {
                                            when {
                                                firstOptions.size < 20 -> firstOptions.add(text)
                                                secondOptions.size < 20 -> secondOptions.add(text)
                                                else -> thirdOptions.add(text)
                                            }
                                        }
                                    }
                                    break
                                } catch (_: Exception) {
                                    continue
                                }
                            }
                            
                            // 팝업 닫기
                            try {
                                driver.findElement(By.cssSelector(".close, .option-close, [class*='close']")).click()
                            } catch (_: Exception) {
                                // ESC 키로 닫기 시도
                                driver.findElement(By.tagName("body")).sendKeys("\u001b")
                            }
                            
                        } catch (_: Exception) {
                            continue
                        }
                    }
                }
                
                if (firstOptions.isNotEmpty()) break
                
            } catch (_: Exception) {
                continue
            }
        }
        
        log.debug("Extracted options - first: {}, second: {}, third: {}", 
                 firstOptions.size, secondOptions.size, thirdOptions.size)
        
        return Triple(firstOptions.toList(), secondOptions.toList(), thirdOptions.toList())
    }
    
    private fun extractCategory(driver: WebDriver): String {
        val selectors = listOf(
            ".breadcrumb li:last-child",
            ".category-path span:last-child",
            "[class*='breadcrumb'] a:last-child",
            ".product-category"
        )
        
        return extractText(driver, selectors) ?: ""
    }
    
    private fun extractGoodsId(url: String): Long {
        val matcher = goodsUrlPattern.matcher(url)
        return if (matcher.matches()) {
            matcher.group(1).toLong()
        } else {
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