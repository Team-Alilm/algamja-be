package org.teamalilm.alilmbe.global.jpa.config

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication

        // 인증된 사용자가 있는지 확인
        if (authentication != null && authentication.principal is UserDetails) {
            val userDetails = authentication.principal as UserDetails
            // 여기서 userDetails.getUserId()는 UserDetails의 커스텀 메서드입니다.
            // 실제 구현에 맞게 사용자 ID를 가져오는 코드를 작성해야 합니다.
            return Optional.ofNullable(userDetails.username) // userId는 커스텀 속성
        }

        return Optional.empty()
    }

}