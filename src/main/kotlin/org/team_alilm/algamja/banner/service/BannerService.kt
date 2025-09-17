package org.team_alilm.algamja.banner.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.banner.controller.dto.response.BannerListResponse
import org.team_alilm.algamja.banner.controller.dto.response.BannerResponse
import org.team_alilm.algamja.banner.repository.BannerRepository

@Service
@Transactional(readOnly = true)
class BannerService(
    private val bannerRepository: BannerRepository
) {

    fun getActiveBanners(): BannerListResponse {
        val banners = bannerRepository.fetchActiveBanners()
        val bannerResponses = banners.map { BannerResponse.from(it) }
        return BannerListResponse.from(bannerResponses)
    }
}