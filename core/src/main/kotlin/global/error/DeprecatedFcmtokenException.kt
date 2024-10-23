package org.team_alilm.global.error

class DeprecatedFcmtokenException : RuntimeException() {
    override val message: String
        get() { return ErrorMessage.DUPLICATE_FCM_TOKEN.code + " : " + ErrorMessage.DUPLICATE_FCM_TOKEN.message }
}