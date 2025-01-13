package org.team_alilm.application.service

import domain.product.Product
import domain.product.ProductId
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.handler.PlatformHandlerResolver

@Service
class ProductSoldoutCheckService(
    private val restClient: RestClient,
    private val platformHandlerResolver: PlatformHandlerResolver,
    @Value("\${jwt-token}") private val jwtToken: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @SqsListener("product-soldout-check-queue")
    fun checkSoldout(payload: Product, @Headers headers: MessageHeaders, acknowledgement: Acknowledgement) {
        try {
        val handle = platformHandlerResolver.resolve(payload.store)
        val soldoutProduct = handle.process(payload)

        if (soldoutProduct) {
            log.info("Product is sold out: $payload")

            val requestBody = RequestBody(productId = payload.id!!.value)
            val response = restClient.put()
                .uri("https://alilm.store/api/v1/baskets/alilm")
                .header("authorization", jwtToken)
                .body(requestBody)
                .retrieve()
                .body(String::class.java)

            log.info("Response from alilm.store: $response")
        }

        acknowledgement.acknowledge()
        } catch (e: Exception) {
            log.error("Error occurred while processing message: $payload", e)
        }
    }

    data class RequestBody(
        val productId: Long
    )
}