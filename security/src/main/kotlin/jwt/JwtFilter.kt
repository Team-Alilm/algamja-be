package org.team_alilm.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import org.team_alilm.global.error.NotFoundMemberException
import org.team_alilm.service.CustomUserDetailsService

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

        try {
            val parserToken = request.getHeader("Authorization")?.replace("Bearer ", "") ?: throw Exception("Token not found")

            if (jwtUtil.validate(parserToken)) {
                val memberId = jwtUtil.getMemberId(parserToken)
                val userDetails = userDetailsService.loadUserByUsername(memberId.toString())
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = authToken
            } else {
                throw Exception("Invalid JWT token")
            }

            filterChain.doFilter(request, response)

        } catch (e: NotFoundMemberException) {
            // 유저가 없을 경우 404 응답
            response.status = HttpServletResponse.SC_NOT_FOUND
            response.writer.write("Member not found")
            response.writer.flush()
        } catch (e: Exception) {
            // 그 외 다른 예외 처리
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized: ${e.message}")
            response.writer.flush()
        }
    }

}