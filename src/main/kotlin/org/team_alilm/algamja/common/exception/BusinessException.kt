package org.team_alilm.algamja.common.exception

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)