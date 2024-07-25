package org.teamalilm.alilmbe.global.quartz.data

data class SoldoutCheckResponse(
    val data: Data
) {
    data class Data(
        val basic: List<BasicOption>,
        val extra: List<ExtraOption> // 추가된 필드
    ) {
        data class BasicOption(
            val name: String,
            val price: Int,
            val isSoldOut: Boolean,
            val remainQuantity: Int,
            val subOptions: List<SubOption>
        ) {
            data class SubOption(
                val name: String,
                val price: Int,
                val isSoldOut: Boolean,
                val remainQuantity: Int,
                val subOptions: List<SubOption>
            )
        }

        data class ExtraOption(
            val someField: String
        )
    }
}