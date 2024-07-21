package org.teamalilm.alilmbe.web.adapter.error

import org.springframework.validation.BindingResult

class RequestValidateException(
    val bindingResult: BindingResult
) : RuntimeException()