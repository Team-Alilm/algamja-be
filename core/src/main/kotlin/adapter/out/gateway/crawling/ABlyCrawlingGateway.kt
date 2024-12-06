package org.team_alilm.adapter.out.gateway.crawling

import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway

@Component
class ABlyCrawlingGateway : CrawlingGateway {

    override fun crawling(request: CrawlingGateway.CrawlingGatewayRequest): Document {
        TODO("Not yet implemented")
    }

}