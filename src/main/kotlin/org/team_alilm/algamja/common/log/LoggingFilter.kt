package org.team_alilm.algamja.common.log

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

class LoggingFilter : OncePerRequestFilter() {

    private val excludedUrls = listOf(
        "html",
        "docs",
        "swagger",
        "config",
        "health",
        "h2-console",
        "favicon.ico",
    )

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // Check if the request URL is excluded
        if (isExcludedUrl(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        // ip
        logger.info("""
            request.remoteAddr : ${request.remoteAddr}
            request.requestURI : ${request.requestURI}
        """.trimIndent())

        // Wrap the request and response
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        // Proceed with the filter chain
        filterChain.doFilter(wrappedRequest, wrappedResponse)

        // Log request and response details
        logRequest(wrappedRequest)
        logResponse(wrappedResponse)

        // Complete the response
        wrappedResponse.copyBodyToResponse()
    }

    private fun isExcludedUrl(requestUri: String): Boolean {
        return excludedUrls.any { requestUri.contains(it) }
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val requestBody = String(request.contentAsByteArray)
        logger.info("""
            Request URL: ${request.requestURI}
            Request Method: ${request.method}
            Request Body: $requestBody
        """.trimIndent())
    }

    private fun logResponse(response: ContentCachingResponseWrapper) {
        val responseBody = String(response.contentAsByteArray)
        logger.info("""
            Response Status: ${response.status}
            Response Body: $responseBody
        """.trimIndent())
    }
}