package org.teamalilm.alilmbe.adapter.out.gateway

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.application.port.out.*
import org.teamalilm.alilmbe.application.port.out.ProductCrawlingGateway.*

@Service
class JsoupProductDataGateway : ProductCrawlingGateway {

    override fun crawling(productDataGatewayRequest: ProductDataGatewayRequest): ProductDataGatewayResponse {
        return Jsoup.connect(productDataGatewayRequest.url).get().let {
            ProductDataGatewayResponse(document = it)
        }
    }

}