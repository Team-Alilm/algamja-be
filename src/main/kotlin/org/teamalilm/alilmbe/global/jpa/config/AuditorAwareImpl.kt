package org.teamalilm.alilmbe.global.jpa.config

import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.http.HttpServletRequest
import java.util.Optional
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.global.security.entity.CustomUserDetails

class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication ?: return Optional.empty()

        return when (authentication.principal) {
            is CustomUserDetails -> {
                val customUserDetails = authentication.principal as CustomUserDetails
                Optional.of(customUserDetails.username)
            }
            is OAuth2User -> {
                val oAuth2User = authentication.principal as OAuth2User
                Optional.of(oAuth2User.name)
            }
            else -> {
                Optional.empty()
            }
        }
    }

}