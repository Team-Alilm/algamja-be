package domain.product

class ProductImage(
    val id: ProductImageId?,
    val imageUrl: String,
    val productNumber: Long,
    val productStore: Store
) {

    data class ProductImageId(val value: Long)
}