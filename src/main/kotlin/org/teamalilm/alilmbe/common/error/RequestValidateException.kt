package org.teamalilm.alilmbe.common.error

import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult

class RequestValidateException(
    private val bindingResult: BindingResult
) : RuntimeException() {

    private val log = LoggerFactory.getLogger(RequestValidateException::class.java)

    override val message: String
        get() {
            val errorMessage = bindingResult.allErrors.joinToString {
                it.defaultMessage ?: ""
            }

            return errorMessage
        }

}
