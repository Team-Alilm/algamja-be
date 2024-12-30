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
        val thumbnailUrl: String,
        val imageUrlList: List<String>,
        val firstCategory: String,
        val secondCategory: String?,
        val price: Int,
        val firstOption: String?,
        val secondOption: String?,
        val thirdOption: String?,
        val member: Member
    )
}


