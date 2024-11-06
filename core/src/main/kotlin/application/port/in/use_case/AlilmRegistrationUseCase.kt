package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Store

interface AlilmRegistrationUseCase {

    fun alilmRegistration(command: AlilmRegistrationCommand)

    data class AlilmRegistrationCommand(
        val number: Long,
        val name: String,
        val brand: String,
        val store: Store,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val member: Member
    )

    fun alilmRegistrationV2(command: AlilmRegistrationCommandV2)

    data class AlilmRegistrationCommandV2(
        val number: Long,
        val name: String,
        val brand: String,
        val store: Store,
        val thumbnailUrl: String,
        val imageUrlList: List<String>,
        val category: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val member: Member
    )
}


