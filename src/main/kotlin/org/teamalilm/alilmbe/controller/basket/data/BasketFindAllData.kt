package org.teamalilm.alilmbe.controller.basket.data


data class BasketFindAllData(
    val id: Long,
    val memberNickname: String,
    val productName: String,
    val productImageUrl: String,
    val productOption1: String,
    val productOption2: String?,
    val productOption3: String?,
    val productSelectCount: Int,
    val createdDate: Long
) {
}