package org.teamalilm.alilmbe.service.product

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository

@Service
class ProductFindAllService(
    private val productRepository: ProductRepository
) {

    fun findAll(productFindAllCommand: ProductFindAllCommand): Slice<ProductFindAllResult> {
        return productRepository.findAllByIsDeleteFalse(productFindAllCommand.pageRequest).map {
            ProductFindAllResult(it.name)
        }
    }

    data class ProductFindAllCommand(
        val pageRequest: PageRequest
    )

    data class ProductFindAllResult(
        val name: String,
    )
}