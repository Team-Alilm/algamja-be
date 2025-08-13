package org.team_alilm.common.log

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1) // TraceIdFilter 보다 뒤/앞 어느 쪽이든 OK. 보통 TraceId 먼저.
class SecurityMdcFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal
        // 예) CustomMemberDetails 에 member.id 가 있다고 가정
        val memberId = (principal as? org.team_alilm.common.security.CustomMemberDetails)
            ?.member?.id?.toString()

        memberId?.let { MDC.put("memberId", it) }

        try {
            chain.doFilter(req, res)
        } finally {
            // traceId는 앞 필터에서 지우므로 여기선 memberId만 제거
            MDC.remove("memberId")
        }
    }
}