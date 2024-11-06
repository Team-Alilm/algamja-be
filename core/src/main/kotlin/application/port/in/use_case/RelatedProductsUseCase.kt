package org.team_alilm.application.port.`in`.use_case

interface RelatedProductsUseCase {
    fun relatedProducts(command: RelatedProductsCommand): RelatedProductsResult

    data class RelatedProductsCommand(
        val productId: Long
    )

    data class RelatedProductsResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val store: String,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long
    )
}