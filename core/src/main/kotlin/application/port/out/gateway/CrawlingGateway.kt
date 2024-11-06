package org.team_alilm.application.port.out.gateway

import org.jsoup.nodes.Document

interface CrawlingGateway {

    fun crawling(request: CrawlingGatewayRequest) : CrawlingGatewayResponse

    data class CrawlingGatewayRequest(
        val url: String
    )

    data class CrawlingGatewayResponse(
        val html: String
    )
}

