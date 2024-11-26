package org.team_alilm.quartz.job.handler

import org.springframework.stereotype.Component
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundStoreException
import org.team_alilm.quartz.job.handler.impl.ABlyHandler
import org.team_alilm.quartz.job.handler.impl.MusinsaHandler

@Component
class PlatformHandlerResolver(
    private val aBlyHandler: ABlyHandler,
    private val musinsaHandler: MusinsaHandler
) {

    fun resolve(store: Store): PlatformHandler {
        return when (store) {
            Store.A_BLY -> aBlyHandler
            Store.MUSINSA -> musinsaHandler
            else -> throw NotFoundStoreException()
        }
    }
}