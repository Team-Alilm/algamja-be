package org.teamalilm.alilmbe.controller.body

data class ExceptionResponseBody(
    val message: String?,
    val code: Int? = null,
    val data: Any? = null
)