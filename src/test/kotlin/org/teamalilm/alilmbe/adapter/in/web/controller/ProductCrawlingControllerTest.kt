package org.teamalilm.alilmbe.adapter.`in`.web.controller

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.teamalilm.alilmbe.adapter.`in`.web.controller.ProductCrawlingController.*

class ProductScrapingRequestValidationTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `should return validation error when URL is blank`() {
        // Given
        val request = ProductScrapingRequest(_url = "")

        // When
        val violations = validator.validate(request)

        // Then
        assertEquals(1, violations.size)
        violations.forEach { it ->
            assertEquals("URL은 필수입니다.", it.message)
        }
    }

}
