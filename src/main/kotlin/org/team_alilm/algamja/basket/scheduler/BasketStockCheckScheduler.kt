package org.team_alilm.algamja.basket.scheduler

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.fcm.repository.FcmTokenExposedRepository
import org.team_alilm.algamja.notification.repository.NotificationExposedRepository
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.email.service.EmailService
import org.team_alilm.algamja.member.repository.MemberExposedRepository
import org.team_alilm.algamja.product.service.ProductStockCheckService

@Component
class BasketStockCheckScheduler(
    private val basketExposedRepository: BasketExposedRepository,
    private val productExposedRepository: ProductExposedRepository,
    private val fcmTokenExposedRepository: FcmTokenExposedRepository,
    private val notificationExposedRepository: NotificationExposedRepository,
    private val firebaseMessaging: FirebaseMessaging,
    private val emailService: EmailService,
    private val memberExposedRepository: MemberExposedRepository,
    private val productStockCheckService: ProductStockCheckService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "basketStockCheck", lockAtMostFor = "9m", lockAtLeastFor = "30s")
    fun checkBasketProductAvailability() {
        val startTime = System.currentTimeMillis()
        log.info("========== Basket Product Stock Check Started ==========")

        try {
            processBasketStockCheck()
            val duration = System.currentTimeMillis() - startTime
            log.info("========== Basket Product Stock Check Completed in {}ms ==========", duration)
        } catch (e: BusinessException) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Basket Stock Check Failed: {} in {}ms ==========", e.errorCode.message, duration)
            throw e
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Unexpected Error in Basket Stock Check in {}ms ==========", duration, e)
            throw BusinessException(ErrorCode.STOCK_CHECK_FAILED, e)
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun processBasketStockCheck() = runBlocking {
        // 트랜잭션 밖에서 데이터 조회
        val basketsToCheck = org.jetbrains.exposed.sql.transactions.transaction {
            basketExposedRepository.fetchBasketsToCheckStock()
        }

        if (basketsToCheck.isEmpty()) {
            log.info("No baskets to check for stock availability")
            return@runBlocking
        }

        log.info("Found {} baskets to check for stock availability", basketsToCheck.size)

        val productIds = basketsToCheck.map { it.productId }.distinct()
        val products = org.jetbrains.exposed.sql.transactions.transaction {
            productExposedRepository.fetchProductsByIds(productIds)
        }
        val productMap = products.associateBy { it.id }

        // CPU 코어 수 기반 병렬 처리 (최대 4개 - API 부하 고려)
        val parallelism = Runtime.getRuntime().availableProcessors().coerceAtMost(4)
        val dispatcher = Dispatchers.IO.limitedParallelism(parallelism)

        val fcmTokenCache = mutableMapOf<Long, String?>()

        val results = basketsToCheck.chunked(20).flatMap { basketBatch ->
            basketBatch.map { basket ->
                async(dispatcher) {
                    try {
                        processBasketItem(basket, productMap[basket.productId], fcmTokenCache)
                    } catch (e: BusinessException) {
                        log.error("Failed to process basket {}: {}", basket.id, e.errorCode.message)
                        ProcessResult.PRODUCT_UNAVAILABLE
                    } catch (e: Exception) {
                        log.error("Unexpected error processing basket {}", basket.id, e)
                        ProcessResult.PRODUCT_UNAVAILABLE
                    }
                }
            }.awaitAll()
        }

        val successCount = results.count { it == ProcessResult.SUCCESS }
        val noTokenCount = results.count { it == ProcessResult.NO_TOKEN }
        val failedCount = results.count { it == ProcessResult.PRODUCT_UNAVAILABLE }

        log.info("Stock check results - Success: {}, No Token: {}, Failed: {}",
            successCount, noTokenCount, failedCount)
    }
    
    @Transactional
    private fun processBasketItem(
        basket: BasketRow,
        product: ProductRow?,
        fcmTokenCache: MutableMap<Long, String?>
    ): ProcessResult {
        if (product == null || product.isDelete) {
            log.debug("Product {} not found or deleted for basket {}", basket.productId, basket.id)
            return ProcessResult.PRODUCT_UNAVAILABLE
        }
        
        // 실시간으로 재고 확인 (API 호출)
        val isCurrentlyAvailable = try {
            productStockCheckService.checkProductAvailability(product)
        } catch (e: Exception) {
            log.error("Failed to check stock for product {}: {}", product.id, e.message)
            // API 호출 실패 시 DB의 isAvailable 값을 폴백으로 사용
            product.isAvailable
        }
        
        if (!isCurrentlyAvailable) {
            log.debug("Product {} is out of stock for basket {}", basket.productId, basket.id)
            // DB의 isAvailable 상태 업데이트
            if (product.isAvailable) {
                productExposedRepository.markProductAsPurchased(product.id)
            }
            return ProcessResult.PRODUCT_UNAVAILABLE
        }
        
        // 재고가 있다고 확인되면 DB 상태도 업데이트
        if (!product.isAvailable) {
            productExposedRepository.updateProductAvailability(product.id, true)
        }
        
        val fcmToken = fcmTokenCache.getOrPut(basket.memberId) {
            fcmTokenExposedRepository.fetchLatestTokenByMemberId(basket.memberId)?.token
        }
        
        if (fcmToken.isNullOrEmpty()) {
            log.debug("No FCM token found for member {}", basket.memberId)
            return ProcessResult.NO_TOKEN
        }
        
        sendStockNotification(basket, product, fcmToken)
        return ProcessResult.SUCCESS
    }
    
    private fun sendStockNotification(
        basket: BasketRow,
        product: ProductRow,
        fcmToken: String
    ) {
        try {
            // FCM 푸시 알림 발송
            val message = buildFcmMessage(product, fcmToken)
            firebaseMessaging.send(message)
            
            // 이메일 알림 발송 (실패해도 전체 프로세스는 계속 진행)
            try {
                val member = memberExposedRepository.fetchById(basket.memberId)
                if (member != null) {
                    emailService.sendStockNotificationEmail(
                        email = member.email,
                        nickname = member.nickname,
                        product = product
                    )
                    log.debug("Email sent successfully for basket {} to {}", basket.id, member.email)
                }
            } catch (e: Exception) {
                log.error("Failed to send email for basket {} - continuing with other notifications", basket.id, e)
            }
            
            // 알림 기록 저장
            notificationExposedRepository.createNotification(
                memberId = basket.memberId,
                productId = product.id
            )
            
            // 장바구니 알림 상태 업데이트
            basketExposedRepository.updateBasketNotification(
                basketId = basket.id,
                isNotification = true,
                notificationDate = System.currentTimeMillis()
            )
            
            // 상품 구매여부 업데이트
            productExposedRepository.markProductAsPurchased(product.id)
            
            log.debug("Successfully sent all notifications for basket {} to member {}", 
                basket.id, basket.memberId)
                
        } catch (e: FirebaseMessagingException) {
            log.error("FCM send failed for basket {} - Error code: {}", basket.id, e.errorCode)
            throw BusinessException(ErrorCode.FCM_SEND_FAILED, e)
        }
    }
    
    private fun buildFcmMessage(
        product: ProductRow,
        fcmToken: String
    ): Message {
        return Message.builder()
            .setToken(fcmToken)
            .setNotification(
                Notification.builder()
                    .setTitle("상품 재입고 알림")
                    .setBody("${product.name} 상품이 재입고되었습니다!")
                    .build()
            )
            .putData("productId", product.id.toString())
            .putData("productName", product.name)
            .putData("productBrand", product.brand)
            .putData("productPrice", product.price.toString())
            .build()
    }
    
    private enum class ProcessResult {
        SUCCESS,
        NO_TOKEN,
        PRODUCT_UNAVAILABLE
    }
}