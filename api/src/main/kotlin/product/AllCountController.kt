package controller.product;

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team_alilm.application.port.`in`.use_case.AllCountUseCase
import org.team_alilm.response.ApiResponse

@RestController
@Tag(name = "상품 전체 수 API", description = "상품 전체 수를 조회하는 API")
@RequestMapping("/api/v1/products")
public class AllCountController(
    private val allCountUseCase: AllCountUseCase
) {

    @Operation(
        summary = "상품 전체 수 조회 API",
        description = """
            상품 전체 수를 조회할 수 있는 API를 제공합니다.
            이 API는 상품의 총 개수를 반환합니다.
        """
    )
    @GetMapping("/all-count")
    fun productAllCount(): ApiResponse<AllCountResponse> {
        val count = allCountUseCase.productAllCount()
        return ApiResponse.success(
            AllCountResponse(
                productAllCount = count
            )
        )
    }

    data class AllCountResponse(
        val productAllCount: Long
    )
}
