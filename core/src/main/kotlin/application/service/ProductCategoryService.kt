package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.team_alilm.application.port.`in`.use_case.ProductCategoryUseCase
import org.team_alilm.application.port.out.LoadProductPort

@Service
class ProductCategoryService(
    private val loadProductPort: LoadProductPort
) : ProductCategoryUseCase {

    override fun productCategory(): List<String> {
        return loadProductPort.loadProductCategories()
    }
}