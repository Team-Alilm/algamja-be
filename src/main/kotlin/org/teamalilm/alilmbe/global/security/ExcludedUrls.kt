package org.teamalilm.alilmbe.global.security

import org.springframework.http.HttpMethod

enum class ExcludedUrls(
    private val _path: String,
    private val _methode: HttpMethod?
) {

    H2("/h2-console/**", null),
    SWAGGER("/swagger-ui/**", null),
    API_DOCS("/api-docs/**", null),
    HEALTH_CHECK("/health-check", HttpMethod.GET),
    FAVICON("/favicon.ico", null),
    BASKET("/api/v1/baskets", HttpMethod.GET);

    val path: String
        get() = _path

    val methode: HttpMethod?
        get() = _methode
}