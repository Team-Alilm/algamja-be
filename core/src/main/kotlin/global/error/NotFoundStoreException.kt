package org.team_alilm.global.error

class NotFoundStoreException : RuntimeException() {

    override val message: String
        get() { return "${ErrorMessage.NOT_FOUND_STORE.code} : ${ErrorMessage.NOT_FOUND_STORE.message}" }
}