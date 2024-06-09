package org.teamalilm.alilmbe.controller.body

data class BaseResponseBody(
    val code: String,
    val data: Any? = null
)