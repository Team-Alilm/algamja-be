package org.team_alilm.algamja.banner.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.algamja.banner.controller.docs.BannerDocs
import org.team_alilm.algamja.banner.controller.dto.response.BannerListResponse
import org.team_alilm.algamja.banner.service.BannerService
import org.team_alilm.algamja.common.response.ApiResponse

@RestController
@RequestMapping("/api/v1/banners")
class BannerController(
    private val bannerService: BannerService
) : BannerDocs {

    @GetMapping
    override fun getActiveBanners(): ApiResponse<BannerListResponse> {
        val response = bannerService.getActiveBanners()
        return ApiResponse.success(response)
    }
}