package org.teamalilm.alilmbe.global.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil(
    @Value("\${spring.jwt.secretKey}")
    private val secret: String,
) {

    private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    fun getMemberId(token: String): Long {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload[MEMBER_ID_KEY].toString().toLong()
    }

    fun validate(token: String?): Boolean {
        return try {
            Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).payload.expiration.after(Date())
        } catch (e: SecurityException) {
            log.info("Invalid JWT signature-SecurityException, 유효하지 않는 JWT 서명 입니다. token : $token");
            false
        } catch (e: ExpiredJwtException) {
            log.info(e.message)
            log.info("Expired JWT token, 만료된 JWT token 입니다. token : $token");
            log.info("Exp Time : ${ Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).payload.expiration}")
            false
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다. token : $token");
            false
        } catch (e: IllegalArgumentException) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다. token : $token");
            false
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT signature-MalformedJwtException, 유효하지 않는 JWT 서명 입니다. token : $token");
            false
        } catch (e: Exception) {
            log.info(e.message)
            false
        }
    }

    fun createJwt(memberId: Long, expireMs: Long): String {
        val now = System.currentTimeMillis()
        log.info("now : $now")

        return "Bearer " + Jwts.builder()
            .claim(MEMBER_ID_KEY, memberId)
            .issuedAt(Date(now))
            .expiration(Date(now - expireMs))
            .signWith(secretKey)
            .compact()
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtUtil::class.java)

        const val MEMBER_ID_KEY = "memberId"
    }
}