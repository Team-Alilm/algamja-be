package org.team_alilm.application.port.out

import org.team_alilm.domain.Product

interface LoadProductsInBasketsPort {

    fun loadProductsInBaskets(): List<Product>

    data class ProductInBasket(
        val id: Long,
        val name: String,
        val number: Number,
        val store: String,
        val imageUrl: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
    )
}