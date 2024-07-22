package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.teamalilm.alilmbe.adapter.`in`.web.controller.ProductRegisteredController.*
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

typealias ProductRegistrationUseCase = (ProductRegistrationCommand) -> Unit

data class ProductRegistrationCommand(
    val number: Long,
    val name: String,
    val brand: String,
    val store: Store,
    val imageUrl: String,
    val category: String,
    val price: Int,
    val option1: String,
    val option2: String?,
    val option3: String?,
    val member: Member
) {

    companion object {
        fun from(request: ProductRegistrationRequest, member: Member): ProductRegistrationCommand {
            return ProductRegistrationCommand(
                number = request.number,
                name = request.name,
                brand = request.brand,
                store = request.store,
                imageUrl = request.imageUrl,
                category = request.category,
                price = request.price,
                option1 = request.option1,
                option2 = request.option2,
                option3 = request.option3,
                member = member
            )
        }
    }
}
