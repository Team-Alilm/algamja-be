package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.teamalilm.alilmbe.service.scraping.ScrapingService

@Tag(name = "scraping", description = "Scraping APIs")
class ScrapingController(
    private val scrapingService: ScrapingService
) {

    @Operation(
        summary = "Scraping API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 추출하는 API 입니다. 
            
            저희 서비스에 데이터를 저장하는 로직이 1도 없습니다.
        """
    )
    @GetMapping("/scraping")
    fun scraping(
        @RequestBody @Valid scrapingRequest: ScrapingRequest,
        bindingResult: BindingResult
    ): ResponseEntity<ScrapingResponse> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(
            
        )
    }

    data class ScrapingRequest(
        @NotBlank
        private val url: String
    )

    @Schema(description = "Alilm 등록을 위한 요청 DTO")
    data class ScrapingResponse(
        private val name: String
    )

}