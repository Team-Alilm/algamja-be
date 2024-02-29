package org.teamalilm.alilmbe.global.dto

import org.teamalilm.alilmbe.global.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.msg
)