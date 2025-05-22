package org.team_alilm.global.error

class DeprecatedFcmtokenException : RuntimeException() {
    override val message: String
        get() { return Error.DUPLICATE_FCM_TOKEN.code + " : " + Error.DUPLICATE_FCM_TOKEN.message }
}