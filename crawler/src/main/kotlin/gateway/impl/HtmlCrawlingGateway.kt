package org.team_alilm.gateway.impl

import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import org.team_alilm.gateway.CrawlingGateway

@Component
class HtmlCrawlingGateway : CrawlingGateway {

    override fun htmlCrawling(request: CrawlingGateway.CrawlingGatewayRequest): CrawlingGateway.CrawlingGatewayResponse {
        val html = Jsoup
            .connect(request.url)
            .get()
            .html()

        return CrawlingGateway.CrawlingGatewayResponse(html)
    }
}