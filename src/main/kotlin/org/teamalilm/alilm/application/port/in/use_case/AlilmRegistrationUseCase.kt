package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.`in`.web.controller.AlilmRegisteredController.*
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

interface AlilmRegistrationUseCase {

    fun alilmRegistration(command: AlilmRegistrationCommand)

    data class AlilmRegistrationCommand(
        val number: Long,
        val name: String,
        val brand: String,
        val store: Product.Store,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val option1: String,
        val option2: String?,
        val option3: String?,
        val member: Member
    ) {

        companion object {
            fun from(request: AlilmRegistrationRequest, member: Member): AlilmRegistrationCommand {
                return AlilmRegistrationCommand(
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
}


