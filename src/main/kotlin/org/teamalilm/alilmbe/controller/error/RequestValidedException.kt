package org.teamalilm.alilmbe.controller.error

import org.springframework.validation.BindingResult

class RequestValidateException(
    val bindingResult: BindingResult
) : RuntimeException() {
    
    override val message: String
        get() = "요청 값이 올바르지 않아요."
}