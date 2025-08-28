import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.team_alilm.AlgamjaApplication
import org.team_alilm.product.crawler.CrawlerRegistry

@SpringBootTest(classes = [AlgamjaApplication::class])
@ActiveProfiles("local")
class AblyCrawlerTest {

    @Autowired
    private lateinit var crawlerRegistry: CrawlerRegistry

    @Test
    fun `test ably crawler with sample URL`() {
        val testUrl = "https://m.a-bly.com/goods/51801138"
        
        println("Testing Ably Crawler with URL: $testUrl")
        
        val crawler = crawlerRegistry.resolve(testUrl)
        println("Found crawler: ${crawler::class.simpleName}")
        
        val normalizedUrl = crawler.normalize(testUrl)
        println("Normalized URL: $normalizedUrl")
        
        val product = crawler.fetch(testUrl)
        println("Crawled Product:")
        println("- Store Number: ${product.storeNumber}")
        println("- Name: ${product.name}")
        println("- Brand: ${product.brand}")
        println("- Price: ${product.price}")
        println("- Store: ${product.store}")
        println("- First Category: ${product.firstCategory}")
        println("- Second Category: ${product.secondCategory}")
        println("- Thumbnail URL: ${product.thumbnailUrl}")
        println("- Image URLs count: ${product.imageUrls.size}")
        println("- First Options: ${product.firstOptions}")
        println("- Second Options: ${product.secondOptions}")
        println("- Third Options: ${product.thirdOptions}")
        
        assert(product.storeNumber == 51801138L)
        assert(product.store == "ABLY")
        assert(product.name.isNotEmpty())
        assert(product.brand.isNotEmpty())
    }
}