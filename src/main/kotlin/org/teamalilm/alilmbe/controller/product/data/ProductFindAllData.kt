import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.Store

data class ProductFindAllData(
    @Schema(description = "상품 id 입니다.", example = "1", nullable = false)
    val productId: Long,

    @Schema(description = "상품명 입니다.", example = "COOL 롱 슬리브 셔츠 STYLE 3 TIPE", nullable = false)
    val name: String,

    @Schema(description = "상품 번호 입니다.", example = "3859221", nullable = false)
    val number: String,

    @Schema(description = "상품 스토어 입니다.", example = "MUSINSA", nullable = false)
    val store: Store,

    @Schema(description = "상품 옵션 1 입니다.", example = "(헤링본)화이트 or S, M", nullable = true)
    val option1: String,

    @Schema(description = "상품 옵션 2 입니다.", example = "(헤링본)화이트 or S, M", nullable = true)
    val option2: String,

    @Schema(description = "상품 생성일 입니다.", example = "2021-08-01T00:00:00", nullable = false)
    val createdDate: LocalDateTime
) {

    companion object {
        fun of(product: Product): ProductFindAllData {
            return ProductFindAllData(
                productId = product.id!!,
                name = product.name,
                number = product.productInfo.number,
                store = product.productInfo.store,
                option1 = product.productInfo.option1,
                option2 = product.productInfo.option2 ?: "",
                createdDate = product.createdDate
            )
        }
    }
}