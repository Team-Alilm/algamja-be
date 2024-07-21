package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

typealias AlilmRegistrationUseCase = (AlilmRegistrationCommand) -> Unit

data class AlilmRegistrationCommand(
    val number: Int,
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
)
