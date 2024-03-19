package org.teamalilm.alilmbe.domain.product.error.advice

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.teamalilm.alilmbe.domain.product.error.code.ErrorCode
import org.teamalilm.alilmbe.domain.product.error.exception.DuplicateProductInBasketException

@RestControllerAdvice
class ProductControllerAdvice {

    @ExceptionHandler(DuplicateProductInBasketException::class)
    fun handleDuplicateProductInBasketException(ex: DuplicateProductInBasketException): ResponseEntity<Any> {
        val errorCode = ErrorCode.DUPLICATE_PRODUCT_IN_BASKET
        val responseBody = mapOf("error" to errorCode.name, "message" to errorCode.message)
        return ResponseEntity.badRequest().body(responseBody)
    }
}