package org.teamalilm.alilmbe.web.adapter.error

import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult

class RequestValidateException(
    private val bindingResult: BindingResult
) : RuntimeException() {

    private val log = LoggerFactory.getLogger(RequestValidateException::class.java)

    override val message: String
        get() {
            val errorMessage = bindingResult.allErrors.joinToString {
                log.error("error log : ${it.defaultMessage}")
                it.defaultMessage ?: ""
            }
            return errorMessage
        }
}
