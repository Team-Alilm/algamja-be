package org.team_alilm.common.exception

class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)