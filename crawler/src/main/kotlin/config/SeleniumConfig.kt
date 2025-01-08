package org.team_alilm.config

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
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver")

        val options = ChromeOptions()
        options.addArguments("window-size=1920,1080")
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
        options.addArguments("--disable-gpu")
        options.setExperimentalOption("excludeSwitches", listOf("enable-automation"))
        options.setExperimentalOption("useAutomationExtension", false)
        options.addArguments("user-data-dir=/path/to/your/chrome/profile") // 기존 브라우저 프로필 사용
        options.setAcceptInsecureCerts(true)

        val driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1))
        return driver
    }
}
