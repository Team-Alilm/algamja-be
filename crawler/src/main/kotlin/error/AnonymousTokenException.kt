package org.team_alilm.error

class AnonymousTokenException : RuntimeException() {
    override val message: String
        get() = "Anonymous token is not allowed"
}