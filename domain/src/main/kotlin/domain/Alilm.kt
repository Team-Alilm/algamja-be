package domain

class Alilm(
    val id: AlilmId,
    val memberId: Long,
    val productId: Long,
    var readYn: Boolean,
    var createdDate: Long
) {

    fun readAlilm() {
        readYn = true
    }

    companion object {
        fun from(basket: Basket): Alilm {
            return Alilm(
                id = AlilmId(null),
                memberId = basket.memberId.value,
                productId = basket.productId,
                readYn = false,
                createdDate = System.currentTimeMillis()
            )
        }
    }

    data class AlilmId(val value: Long?)
}