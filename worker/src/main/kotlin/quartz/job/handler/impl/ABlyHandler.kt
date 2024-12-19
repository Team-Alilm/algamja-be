package org.team_alilm.quartz.job.handler.impl

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.service.NotificationService
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.job.handler.PlatformHandler


@Component
class ABlyHandler(
    private val restTemplate: RestTemplate,
    private val notificationService: NotificationService
) : PlatformHandler {

    override fun process(product: Product) {
        if(checkSoldOut(product).not()) {
            notificationService.sendNotifications(product)
        }
    }

    private fun checkSoldOut(product: Product): Boolean {
        val ablyAnonymousToken = StringConstant.ABLY_ANONYMOUS_TOKEN
        val headers = HttpHeaders().apply {
            set("x-anonymous-token", ablyAnonymousToken.get())
        }
        // HttpEntity 생성
        val entity = HttpEntity<Any>(headers)
        var response: ResponseEntity<Option> = restTemplate.exchange(
            StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(product.number, 1),
            HttpMethod.GET,
            entity,
            Option::class.java
        )

        val optionComponent = response.body
            ?.option_components
            ?.first { it.name === product.firstOption }

        if (optionComponent?.is_final_depth == false) {
            response = restTemplate.exchange(
                StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(product.number, 2) + "&selected_option_sno=${optionComponent.goods_option_sno}",
                HttpMethod.GET,
                entity,
                Option::class.java
            )

            val optionComponent2 = response.body
                ?.option_components
                ?.first { it.name === product.secondOption }

            if (optionComponent2?.is_final_depth == false) {
                response = restTemplate.exchange(
                    StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(product.number, 3) + "&selected_option_sno=${optionComponent2.goods_option_sno}",
                    HttpMethod.GET,
                    entity,
                    Option::class.java
                )

                val optionComponent3 = response.body
                    ?.option_components
                    ?.first { it.name === product.thirdOption }

                if (optionComponent3?.goods_option?.is_soldout == true) {
                    return true
                }
            } else {
                if (optionComponent2?.goods_option?.is_soldout == true) {
                    return true
                }
            }
        } else {
            if (optionComponent?.goods_option?.is_soldout == true) {
                return true
            }
        }

        return false
    }

    data class Option(
        val option_components: List<OptionComponents>,
        val name: String
    )

    data class OptionComponents(
        val sno: Long,
        val depth: Int,
        val name: String,
        val is_final_depth: Boolean,
        val goods_option_sno: Long,
        val delivery_type: String,
        val standard_delivery_message: String,
        val delivery_lead_days: Any?, // null 가능
        val goods_option: GoodsOption, // null 가능
        val wholesale_name: Any?, // null 가능
        val has_high_demand_tag: Boolean
    )

    data class GoodsOption(
        val sno: String,
        val option_names: List<String>,
        val original_price: Int,
        val price: Int,
        val point: Int,
        val is_soldout: Boolean,
        val is_possible_applying_restock_noti: Boolean,
        val is_applied_for_restock: Boolean,
        val stock: Int,
        val delivery_type: String,
        val os_delayed: Boolean,
    )
}