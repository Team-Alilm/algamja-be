package org.team_alilm.algamja.common.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.team_alilm.algamja.common.security.CustomUserDetailsService

class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {

    // 1) 특정 요청은 아예 필터링하지 않기
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        if (request.method.equals("OPTIONS", ignoreCase = true)) return true
        if (uri.startsWith("/login/") || uri.startsWith("/oauth2/")) return true
        return false
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = request.getHeader("Authorization")?.removePrefix("Bearer ")?.trim()

            if (!token.isNullOrBlank() && jwtUtil.validate(token)) {
                val memberId = jwtUtil.getMemberId(token)
                val userDetails = userDetailsService.loadUserByUsername(memberId.toString())
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = authToken
            }

        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized: ${e.message}")
            response.writer.flush()
            return
        }

        filterChain.doFilter(request, response)
    }
}