package org.team_alilm.error

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
