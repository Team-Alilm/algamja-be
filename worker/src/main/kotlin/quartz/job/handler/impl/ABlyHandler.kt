package org.team_alilm.quartz.job.handler.impl

import org.springframework.stereotype.Component
import org.team_alilm.domain.product.Product
import org.team_alilm.quartz.job.handler.PlatformHandler

@Component
class ABlyHandler : PlatformHandler {

    override fun process(product: Product): Boolean {
        return null === product.price
    }
}