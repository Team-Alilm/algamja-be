package org.teamalilm.alilm.global.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.teamalilm.alilm.application.service.security.CustomUserDetailsService

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val parserToken = request.getHeader("Authorization")?.replace("Bearer ", "") ?: ""

        if (jwtUtil.validate(parserToken)) {
            val memberId = jwtUtil.getMemberId(parserToken)

            val userDetails = userDetailsService.loadUserByUsername(memberId.toString())

            val authToken =
                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

            SecurityContextHolder.getContext().authentication = authToken
        } else {
            // JWT가 유효하지 않으면 401 응답을 반환하고 필터 체인 중단
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized")
            response.writer.flush()
            return
        }

        filterChain.doFilter(request, response)
    }

}