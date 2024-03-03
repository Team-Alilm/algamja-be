package org.teamalilm.alilmbe.global.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil(
    @Value("\${spring.jwt.secretKey}")
    private val secret: String
) {

    private val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().algorithm)

    fun getMemberId(token: String): Long {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload["memberid", Long::class.java]
    }

    fun isExpired(token: String?): Boolean {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.expiration.before(Date())
    }

    fun createJwt(memberId: Long, expireMs: Long) : String {

        return Jwts.builder()
            .claim("memberId", memberId)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + memberId))
            .signWith(secretKey)
            .compact()
    }
}