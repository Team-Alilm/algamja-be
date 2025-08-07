package org.team_alilm.common.security.oauth

import domain.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import org.team_alilm.common.security.oauth.OAuth2Attribute
import java.util.Collections

@Component
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        // kakao
        val registrationId = userRequest.clientRegistration.registrationId

        // id
        val userNameAttributeName = userRequest
            .clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        val oAuth2Attributes = oAuth2User.attributes

        val oAuth2Attribute = OAuth2Attribute.Companion.of(
            attributes = oAuth2Attributes,
            provider = registrationId,
            attributeKey = userNameAttributeName
        )

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(Role.RoleType.ROLE_USER.name)),
            oAuth2Attribute.convertToMap(),
            userNameAttributeName
        )

    }

}