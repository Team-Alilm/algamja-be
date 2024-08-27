package org.teamalilm.alilm.global.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
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
        }

        filterChain.doFilter(request, response)
    }

}