package org.team_alilm.application.port.out.gateway

import org.jsoup.nodes.Document

// todo : Jsoup에 의존적인 코드 입니다.
interface CrawlingGateway {

    fun crawling(request: org.team_alilm.application.port.out.gateway.CrawlingGateway.CrawlingGatewayRequest) : org.team_alilm.application.port.out.gateway.CrawlingGateway.CrawlingGatewayResponse

    data class CrawlingGatewayRequest(
        val url: String
    )

    data class CrawlingGatewayResponse(
        val document: Document
    )
}

