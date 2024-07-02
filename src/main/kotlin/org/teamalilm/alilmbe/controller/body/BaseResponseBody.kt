package org.teamalilm.alilmbe.controller.body

import org.springframework.http.HttpStatus

data class ExceptionResponseBody(
    val message: String?,
    val code: Int? = null,
    val data: Any? = null
)