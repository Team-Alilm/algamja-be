package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Product

interface SoldOutProductPort {

    fun soldOut(product: Product)

}