package org.team_alilm.gateway

interface CrawlingGateway {

    fun htmlCrawling(request: CrawlingGatewayRequest) : CrawlingGatewayResponse

    data class CrawlingGatewayRequest(
        val url: String,
    )

    data class CrawlingGatewayResponse(
        val html: String,
    )
}

