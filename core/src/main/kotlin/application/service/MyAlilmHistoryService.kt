package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryCountUseCase
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryUseCase
import org.team_alilm.application.port.out.LoadMyAlilmHistoryPort
import java.time.LocalDate
import java.time.ZoneOffset

@Service
@Transactional
class MyAlilmHistoryService(
    val loadMyAlilmHistoryPort: LoadMyAlilmHistoryPort,
    val loadMyAlilmHistoryCountPort: LoadMyAlilmHistoryPort
) : MyAlilmHistoryUseCase, MyAlilmHistoryCountUseCase {

    override fun myAlilmHistory(command: MyAlilmHistoryUseCase.MyAlilmHistoryCommand): List<MyAlilmHistoryUseCase.MyAlilmHistoryResult> {
        val dayLimit: Long = LocalDate.now().plusDays(-31).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val myAlilmAndProductList = loadMyAlilmHistoryPort.loadMyAlilmHistory(command.member, dayLimit)

        return myAlilmAndProductList.map {
            MyAlilmHistoryUseCase.MyAlilmHistoryResult(
                alilmId = it.alilm.id.value!!,
                productid = it.product.id!!,
                productId = it.product.id!!,
                name = it.product.name,
                imageUrl = it.product.thumbnailUrl,
                brand = it.product.brand,
                price = it.product.price,
                firstOption = it.product.firstOption,
                secondOption = it.product.secondOption,
                thirdOption = it.product.thirdOption,
                readYn = it.alilm.readYn,
                createdDate = it.alilm.createdDate,
            )
        }
    }

    override fun myAlilmHistoryCount(command: MyAlilmHistoryCountUseCase.MyAlilmHistoryCountCommand): MyAlilmHistoryCountUseCase.MyAlilmHistoryCountResult {
        val dayLimit: Long = LocalDate.now().plusDays(-31).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val myAlilmHistoryCount = loadMyAlilmHistoryCountPort.loadMyAlilmHistoryCount(command.member, dayLimit)

        return MyAlilmHistoryCountUseCase.MyAlilmHistoryCountResult(
            readYCount = myAlilmHistoryCount.readNCount
        )
    }

}