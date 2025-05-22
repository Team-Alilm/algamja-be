package org.team_alilm.global.error

class BusinessException(
    val errorCode : ErrorCode,
) : RuntimeException(errorCode.message)