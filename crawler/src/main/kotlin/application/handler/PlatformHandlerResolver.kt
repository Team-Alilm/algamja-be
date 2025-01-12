package org.team_alilm.application.handler

import org.springframework.stereotype.Component
import domain.product.Store
import org.team_alilm.application.handler.impl.ABlyHandler
import org.team_alilm.application.handler.impl.CM29Handler
import org.team_alilm.application.handler.impl.MusinsaHandler

@Component
class PlatformHandlerResolver(
//    private val aBlyHandler: ABlyHandler,
    private val musinsaHandler: MusinsaHandler,
    private val cm29Handler: CM29Handler,
) {

    fun resolve(store: Store): PlatformHandler {
        return when (store) {
            Store.CM29 -> cm29Handler
//            Store.A_BLY -> aBlyHandler
            Store.MUSINSA -> musinsaHandler
            else -> throw IllegalArgumentException()
        }
    }
}