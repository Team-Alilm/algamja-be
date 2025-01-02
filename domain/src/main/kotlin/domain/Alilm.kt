package domain

class Alilm(
    val id: AlilmId,
    val memberId: Long,
    val productId: Long,
) {

    companion object {
        fun from(basket: Basket): Alilm {
            return Alilm(
                id = AlilmId(null),
                memberId = basket.memberId.value,
                productId = basket.productId.value,
            )
        }
    }

    data class AlilmId(val value: Long?)
}