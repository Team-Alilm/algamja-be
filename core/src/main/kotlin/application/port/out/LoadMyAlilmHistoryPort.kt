package org.team_alilm.application.port.out

import domain.Alilm
import domain.Member
import domain.product.Product

interface LoadMyAlilmHistoryPort {

    fun loadMyAlilmHistory(member: Member, dayLimit: Long): List<MyAlilmHistory>

    data class MyAlilmHistory(
        val alilm: Alilm,
        val product: Product
    ) {
        companion object {
            fun of (alilm: Alilm, product: Product): MyAlilmHistory {
                return MyAlilmHistory(
                    alilm = alilm,
                    product = product
                )
            }
        }
    }

    fun loadMyAlilmHistoryCount(member: Member, dayLimit: Long): MyAlilmHistoryCount
    data class MyAlilmHistoryCount(
        val readNCount: Long
    )
}