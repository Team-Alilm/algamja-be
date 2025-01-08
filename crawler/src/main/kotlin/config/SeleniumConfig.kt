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
        val options = ChromeOptions()
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")
        options.addArguments("--window-size=1920,1080")
        options.addArguments("--ignore-certificate-errors")

        options.setAcceptInsecureCerts(true)

        val driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1))
        return driver
    }
}
