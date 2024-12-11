package org.team_alilm.quartz.job.handler

import org.team_alilm.domain.product.Product

interface PlatformHandler {

    fun process(product: Product)
}