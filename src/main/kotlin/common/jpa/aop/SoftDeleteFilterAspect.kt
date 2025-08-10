package org.team_alilm.common.jpa.filter

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Pointcut
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class SoftDeleteFilterAspect(
    private val filterEnabler: HibernateFilterEnabler
) {
    @Pointcut("execution(* org.team_alilm..controller..*(..))")
    fun anyController() {}

    @Before("anyController()")
    fun applyFilterByRole() {
        val auth = SecurityContextHolder.getContext().authentication
        val isAdmin = auth?.authorities?.any { it.authority == "ROLE_ADMIN" } ?: false

        if (isAdmin) {
            // 어드민 → 삭제 데이터 포함
            filterEnabler.disableDeletedFilter()
        } else {
            // 일반 유저 → 삭제 제외
            filterEnabler.enableNotDeletedOnly()
        }
    }

    @After("anyController()")
    fun resetFilter() {
        // 요청 끝나면 필터 해제
        filterEnabler.disableDeletedFilter()
    }
}