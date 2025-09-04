package org.team_alilm.algamja.product.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option.OptionApiResponse
import org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option.OptionItem
import org.team_alilm.algamja.product.entity.ProductRow

@Service
class ProductStockCheckService(
    private val restClient: RestClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun checkProductAvailability(product: ProductRow): Boolean {
        return when (product.store) {
            Store.MUSINSA -> checkMusinsaAvailability(product)
            Store.ZIGZAG -> checkZigzagAvailability(product)
            Store.CM29 -> checkCm29Availability(product)
        }
    }

    private fun checkMusinsaAvailability(product: ProductRow): Boolean {
        return try {
            val response = fetchMusinsaOptions(product.storeNumber) ?: return false
            val optionItems = response.data?.optionItems ?: return logAndReturnFalse(
                "No option items for Musinsa product: ${product.storeNumber}"
            )
            
            val targetOption = if (product.firstOption.isNotEmpty()) {
                findMatchingOption(optionItems, product)
            } else {
                optionItems.firstOrNull()
            }
            
            targetOption?.let { 
                checkOptionAvailability(it, product.storeNumber) 
            } ?: false
            
        } catch (e: Exception) {
            log.error("Failed to check Musinsa product availability: ${product.storeNumber}", e)
            false
        }
    }
    
    private fun fetchMusinsaOptions(storeNumber: Long): OptionApiResponse? {
        val uri = "https://goods-detail.musinsa.com/api2/goods/$storeNumber/v2/options?goodsSaleType=SALE"
        val response = restClient.get()
            .uri(uri)
            .retrieve()
            .body(OptionApiResponse::class.java)
        
        if (response?.data == null) {
            log.warn("Failed to get option data for Musinsa product: $storeNumber")
        }
        return response
    }
    
    private fun findMatchingOption(
        optionItems: List<OptionItem>,
        product: ProductRow
    ): OptionItem? {
        val matchingOption = optionItems.find { optionItem ->
            matchesProductOptions(optionItem, product)
        }
        
        if (matchingOption == null) {
            log.warn("Option not found for product: ${product.storeNumber}, " +
                    "options: ${product.firstOption}/${product.secondOption}/${product.thirdOption}")
        }
        return matchingOption
    }
    
    private fun matchesProductOptions(
        optionItem: OptionItem,
        product: ProductRow
    ): Boolean {
        val optionNames = optionItem.optionValues.map { it.name }
        return optionNames.contains(product.firstOption) &&
               (product.secondOption.isNullOrEmpty() || optionNames.contains(product.secondOption)) &&
               (product.thirdOption.isNullOrEmpty() || optionNames.contains(product.thirdOption))
    }
    
    private fun checkOptionAvailability(
        option: OptionItem,
        storeNumber: Long
    ): Boolean {
        val isAvailable = option.activated && 
                         !option.outOfStock && 
                         !option.isSoldOut && 
                         !option.isDeleted &&
                         option.remainQuantity > 0
        
        log.debug("Musinsa product $storeNumber availability: $isAvailable " +
                 "(remainQuantity: ${option.remainQuantity})")
        return isAvailable
    }
    
    private fun logAndReturnFalse(message: String): Boolean {
        log.warn(message)
        return false
    }


    private fun checkZigzagAvailability(product: ProductRow): Boolean {
        // TODO: Zigzag API 구현
        log.debug("Zigzag stock check not implemented yet for product: ${product.storeNumber}")
        return false
    }

    private fun checkCm29Availability(product: ProductRow): Boolean {
        // TODO: 29CM API 구현  
        log.debug("29CM stock check not implemented yet for product: ${product.storeNumber}")
        return false
    }
}