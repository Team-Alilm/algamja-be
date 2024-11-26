package org.team_alilm.quartz.job.handler

import org.team_alilm.domain.product.Product

interface PlatformHandler {

    // 상품이 품절 여부에 따른 동작
    fun process(product: Product): Boolean
}
