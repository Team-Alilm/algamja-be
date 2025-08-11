package org.team_alilm.product.controller.dto.param

import io.swagger.v3.oas.annotations.media.Schema
import org.team_alilm.common.enums.Sort

@Schema(description = "상품 목록 조회 요청 파라미터")
data class ProductListParam(

    @Schema(description = "검색 키워드", example = "노트북")
    val keyword: String? = null,

    @Schema(description = "페이지당 조회 개수 (기본값 20)", example = "20", defaultValue = "20")
    val size: Int = 20,

    @Schema(description = "카테고리 코드", example = "ELECTRONICS")
    val category: String? = null,

    @Schema(description = "정렬 방식", example = "LATEST")
    val sort: Sort,

    @Schema(description = "마지막 상품 ID (무한 스크롤용)", example = "100")
    val lastProductId: Long? = null,

    @Schema(description = "대기 인원 수 필터", example = "5")
    val lastWaitingCount: Long? = null,

    @Schema(description = "가격 필터", example = "100000")
    val lastPrice: Int? = null
)