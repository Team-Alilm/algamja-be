package org.team_alilm.algamja.common.exception

import org.team_alilm.algamja.common.exception.ErrorCode

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)