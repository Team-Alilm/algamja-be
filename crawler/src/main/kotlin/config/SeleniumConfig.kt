package org.team_alilm.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class SeleniumConfig {

    @Bean
    fun webDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()  // 먼저 WebDriver 설정

        val options = ChromeOptions()
        options.addArguments("--headless")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")
        options.addArguments("--window-size=1920,1080")
        options.addArguments("--ignore-certificate-errors")
        options.setAcceptInsecureCerts(true)

        // User-Agent 변경
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")

        val driver = ChromeDriver(options)

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2))

        return driver
    }
}


