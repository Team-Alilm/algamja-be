package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Store

interface LoadProductImagePort {

    fun existsByProductImage(productNumber: Long, productStore: Store): Boolean
}