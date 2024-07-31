package org.teamalilm.alilmbe.application.port.out

import org.jsoup.nodes.Document

// todo : ProductDataGateway를 사용하는 UseCase에서 사용할 수 있도록 ProductDataGatewayResponse를 반환하는 함수 타입을 정의하세요.
interface ProductCrawlingGateway {

    fun crawling(productDataGatewayRequest: ProductDataGatewayRequest) : ProductDataGatewayResponse

    data class ProductDataGatewayRequest(
        val url: String
    )

    data class ProductDataGatewayResponse(
        val document: Document
    )
}

