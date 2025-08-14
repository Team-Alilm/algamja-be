package org.team_alilm.product.controller.v1.dto.param

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.team_alilm.common.enums.Sort

@Schema(description = "상품 목록 조회 요청 파라미터")
data class ProductListParam(

    @field:Size(max = 100, message = "검색 키워드는 최대 100자까지 입력 가능합니다.")
    @Schema(description = "검색 키워드", example = "노트북", maxLength = 100)
    val keyword: String? = null,

    @field:Min(value = 1, message = "페이지당 조회 개수는 1 이상이어야 합니다.")
    @field:Max(value = 100, message = "페이지당 조회 개수는 100 이하이어야 합니다.")
    @Schema(description = "페이지당 조회 개수 (기본값 20)", example = "20", defaultValue = "20", minimum = "1", maximum = "100")
    val size: Int = 20,

    @field:Size(max = 50, message = "카테고리 코드는 최대 50자까지 입력 가능합니다.")
    @Schema(description = "카테고리 코드", example = "ELECTRONICS", maxLength = 50)
    val category: String? = null,

    @field:NotNull(message = "정렬 방식은 필수 값입니다.")
    @Schema(description = "정렬 방식", example = "LATEST", required = true)
    val sort: Sort,

    @field:Min(value = 1, message = "마지막 상품 ID는 1 이상이어야 합니다.")
    @Schema(description = "마지막 상품 ID (무한 스크롤용)", example = "100", minimum = "1")
    val lastProductId: Long? = null,

    @field:Min(value = 0, message = "대기 인원 수 필터는 0 이상이어야 합니다.")
    @Schema(description = "대기 인원 수 필터", example = "5", minimum = "0")
    val lastWaitingCount: Long? = null,

    @field:Min(value = 0, message = "가격 필터는 0 이상이어야 합니다.")
    @Schema(description = "가격 필터", example = "100000", minimum = "0")
    val lastPrice: Int? = null
)