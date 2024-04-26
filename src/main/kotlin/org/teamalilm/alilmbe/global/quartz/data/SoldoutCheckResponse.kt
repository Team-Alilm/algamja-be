package org.teamalilm.alilmbe.global.quartz.data


data class SoldoutCheckResponse(
    val data: Data
) {

    data class Data(
        val basic: List<BasicOption>,
    ) {

        data class BasicOption(
            val name: String,
            val price: Int,
            val isSoldOut: Boolean,
            val remainQuantity: Int,
            val subOptions: List<SubOption>  // SubOptions가 정확한 데이터 형식을 알 수 없어서 일단 Any로 정의
        ) {

            data class SubOption(
                val name: String,
                val price: Int,
                val isSoldOut: Boolean,
                val remainQuantity: Int,
            )
        }
    }
}