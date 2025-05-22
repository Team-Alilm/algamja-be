package org.team_alilm.global.error

class DeprecatedFcmtokenException : RuntimeException() {
    override val message: String
        get() { return ErrorCode.DUPLICATE_FCM_TOKEN.code + " : " + ErrorCode.DUPLICATE_FCM_TOKEN.message }
}