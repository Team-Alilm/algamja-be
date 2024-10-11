package org.team_alilm.application.port.out

import org.team_alilm.domain.Price
import org.team_alilm.domain.Product

interface AddPricePort {

    fun addPrice(
        price: Int,
        product: Product
    ): Price

}