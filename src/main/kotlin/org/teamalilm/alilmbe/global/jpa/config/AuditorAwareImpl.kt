package org.teamalilm.alilmbe.global.jpa.config

import jakarta.servlet.http.HttpServletRequest
import java.util.Optional
import org.springframework.data.domain.AuditorAware

class AuditorAwareImpl(
    val httpServletRequest: HttpServletRequest
) : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        return
    }

}