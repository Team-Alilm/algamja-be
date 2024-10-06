package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Price
import org.teamalilm.alilm.domain.Product

interface AddPricePort {

    fun addPrice(
        price: Int,
        product: Product
    ): Price

}