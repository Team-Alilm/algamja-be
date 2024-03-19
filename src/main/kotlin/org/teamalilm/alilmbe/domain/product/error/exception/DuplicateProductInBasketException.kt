package org.teamalilm.alilmbe.domain.product.error.exception

import org.teamalilm.alilmbe.domain.product.error.code.ErrorCode

class DuplicateProductInBasketException : RuntimeException(ErrorCode.DUPLICATE_PRODUCT_IN_BASKET.message) {

}