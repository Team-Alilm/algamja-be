package org.teamalilm.alilmbe.global.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.teamalilm.alilmbe.global.service.CustomUserDetailsService

class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("token")

        if (jwtUtil.isExpired(token)) {
            val memberId = jwtUtil.getMemberId(token)

            val userDetails = customUserDetailsService.loadUserByMemberId(memberId)
            val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

            SecurityContextHolder.getContext().authentication = authToken
        }

        filterChain.doFilter(request, response)
    }

}