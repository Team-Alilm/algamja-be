package org.teamalilm.alilmbe.adapter.out.gateway

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.application.port.out.*

@Service
class JsoupProductDataGateway : ProductDataGateway {

    override fun invoke(request: ProductDataGatewayRequest): ProductDataGatewayResponse {
        return Jsoup.connect(request.url).get().let {
            ProductDataGatewayResponse(document = it)
        }
    }

}