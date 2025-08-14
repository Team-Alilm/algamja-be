package org.team_alilm.common.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProps(
    var origins: List<String> = emptyList(),
    var methods: List<String> = listOf("GET","POST","PUT","DELETE","PATCH","OPTIONS"),
    var headers: List<String> = listOf("*"),
    var allowCredentials: Boolean = false,   // allow-credentials ← 자동 매핑됨(릴랙스드 바인딩)
    var maxAge: Long = 3600                   // max-age ← 자동 매핑됨
)