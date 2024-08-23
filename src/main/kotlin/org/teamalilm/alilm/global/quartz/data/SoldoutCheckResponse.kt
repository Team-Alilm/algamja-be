package org.teamalilm.alilm.global.quartz.data

data class SoldoutCheckResponse(
    val meta: Meta,
    val data: Data,
    val error: String? // JSON에서 error는 null일 수 있으므로 nullable로 정의합니다.
) {
    data class Meta(
        val result: String,
        val errorCode: String,
        val message: String
    )

    data class Data(
        val basic: List<BasicOption>,
        val extra: List<Any>, // JSON에서 extra는 빈 배열로 되어 있으며, 실제 사용에 맞게 조정할 수 있습니다.
        val optionItems: List<OptionItem> // JSON에서 optionItems가 포함되어 있습니다.
    ) {
        data class BasicOption(
            val no: Int,
            val type: String,
            val displayType: String,
            val name: String,
            val standardOptionNo: Int,
            val sequence: Int,
            val isDeleted: Boolean,
            val optionValues: List<OptionValue>
        )

        data class OptionValue(
            val no: Int,
            val optionNo: Int,
            val name: String,
            val code: String,
            val sequence: Int,
            val standardOptionValueNo: Int,
            val color: ColorDetail?, // color 필드를 ColorDetail?로 정의
            val isDeleted: Boolean
        )

        data class OptionItem(
            val no: Int,
            val goodsNo: Int,
            val optionValueNos: List<Int>,
            val managedCode: String,
            val price: Int,
            val activated: Boolean,
            val outOfStock: Boolean,
            val isDeleted: Boolean,
            val optionValues: List<OptionValueDetail>,
            val colors: List<Color>,
            val remainQuantity: Int
        )

        data class OptionValueDetail(
            val no: Int,
            val name: String,
            val code: String,
            val optionNo: Int,
            val optionName: String
        )

        data class Color(
            val optionItemNo: Int,
            val colorCode: String,
            val colorType: String
        )

        data class ColorDetail( // ColorDetail 클래스 추가
            val colorCode: String,
            val colorType: String
        )
    }
}
