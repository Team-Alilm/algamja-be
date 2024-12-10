package org.team_alilm.adapter.out.gateway.crawling

import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway.CrawlingGatewayResponse

@Component
class ABlyCrawlingGateway : CrawlingGateway {

    override fun crawling(request: CrawlingGateway.CrawlingGatewayRequest): CrawlingGatewayResponse {
        // Todo: Implement crawling logic
        return CrawlingGatewayResponse(
            html = ""
        )
    }

}