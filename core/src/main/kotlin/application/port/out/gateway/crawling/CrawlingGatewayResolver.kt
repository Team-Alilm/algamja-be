package org.team_alilm.application.port.out.gateway.crawling

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.gateway.crawling.ABlyCrawlingGateway
import org.team_alilm.adapter.out.gateway.crawling.MusinsaCrawlingGateway
import org.team_alilm.domain.product.Store

@Component
class CrawlingGatewayResolver(
    private val musinsaCrawlingGateway: MusinsaCrawlingGateway,
    private val aBlyCrawlingGateway: ABlyCrawlingGateway
) {

    fun resolve(store: Store): CrawlingGateway {
        return when (store) {
            Store.MUSINSA -> musinsaCrawlingGateway
            Store.A_BLY -> aBlyCrawlingGateway
        }
    }
}