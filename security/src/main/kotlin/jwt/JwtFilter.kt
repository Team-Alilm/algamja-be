package org.team_alilm.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.team_alilm.CustomUserDetailsService

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
    private val excludedPaths: List<String> // 제외할 경로 리스트 주입
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (excludedPaths.any { AntPathRequestMatcher(it).matches(request) }) {
            filterChain.doFilter(request, response)
            return
        }

        // 이하 JWT 검증 로직...
        val parserToken = request.getHeader("Authorization")?.replace("Bearer ", "") ?: ""

        if (jwtUtil.validate(parserToken)) {
            val memberId = jwtUtil.getMemberId(parserToken)
            val userDetails = userDetailsService.loadUserByUsername(memberId.toString())
            val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authToken
        } else {
            // JWT가 유효하지 않으면 401 응답 반환
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized")
            response.writer.flush()
            return
        }

        filterChain.doFilter(request, response)
    }
}