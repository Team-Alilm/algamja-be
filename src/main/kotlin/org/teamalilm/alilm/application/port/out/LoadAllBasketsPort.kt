package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

interface LoadAllBasketsPort {

    fun loadAllBaskets() : List<BasketAndMemberAndProduct>

    data class BasketAndMemberAndProduct(
        val basket: Basket,
        val member: Member,
        val product: Product
    ) {

        fun getManagedCode() : String {
            return if (product.firstOption.isNotBlank() && product.secondOption?.isNotBlank() == true && product.thirdOption?.isNotBlank() == true) {
                "${product.firstOption}^${product.secondOption}^${product.thirdOption}"
            } else if (product.firstOption.isNotBlank() && product.secondOption?.isNotBlank() == true) {
                "${product.firstOption}^${product.secondOption}"
            } else {
                product.firstOption
            }
        }
    }

}