package org.team_alilm.algamja.banner.controller.dto.response

data class BannerListResponse(
    val banners: List<BannerResponse>
) {
    companion object {
        fun from(banners: List<BannerResponse>): BannerListResponse {
            return BannerListResponse(banners = banners)
        }
    }
}