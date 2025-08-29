package org.team_alilm.algamja.common.exception

class BusinessException(
    val errorCode: ErrorCode,
    cause: Throwable? = null
) : RuntimeException(errorCode.message, cause)