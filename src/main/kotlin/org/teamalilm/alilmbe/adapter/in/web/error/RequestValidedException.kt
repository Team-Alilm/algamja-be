package org.teamalilm.alilmbe.web.adapter.error

import org.springframework.validation.BindingResult

class RequestValidateException(
    val bindingResult: BindingResult
) : RuntimeException() {

    override val message: String
        get() {
            val errorMessage = bindingResult.allErrors.joinToString { it.defaultMessage ?: "" }
            return errorMessage
        }
}
