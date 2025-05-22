package org.team_alilm.global.error

class BusinessException(
    val error : Error,
) : RuntimeException(error.message)