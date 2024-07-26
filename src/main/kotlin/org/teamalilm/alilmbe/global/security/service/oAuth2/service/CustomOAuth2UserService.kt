package org.teamalilm.alilmbe.global.security.service.oAuth2.service

import java.util.Collections
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.OAuth2Attribute

@Component
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        // kakao
        val registrationId = userRequest.clientRegistration.registrationId

        // id
        val userNameAttributeName = userRequest
            .clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        val oAuth2Attributes = oAuth2User.attributes

        val oAuth2Attribute = OAuth2Attribute.of(
            attributes = oAuth2Attributes,
            provider = registrationId,
            attributeKey = userNameAttributeName
        )

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(Member.Role.MEMBER.key)),
            oAuth2Attribute.convertToMap(),
            userNameAttributeName
        )

    }

}