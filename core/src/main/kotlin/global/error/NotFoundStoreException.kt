package org.team_alilm.global.error

class NotFoundStoreException : RuntimeException() {

    override val message: String
        get() { return "${Error.NOT_FOUND_STORE.code} : ${Error.NOT_FOUND_STORE.message}" }
}