package org.team_alilm.error.exception

import org.team_alilm.error.code.MusinsaErrorCode

class MusinsaCrawlingException(
    val errorCode: MusinsaErrorCode
) : RuntimeException(errorCode.message) {
    val code: Int = errorCode.code
}
