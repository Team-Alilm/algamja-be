package org.team_alilm.common.exception

import common.exception.ErrorCode

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)