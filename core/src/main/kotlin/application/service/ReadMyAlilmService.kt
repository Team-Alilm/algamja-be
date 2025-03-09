package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyAlilmReadAllUseCase
import org.team_alilm.application.port.`in`.use_case.MyAlilmReadUseCase
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.application.port.out.LoadAlilmPort
import org.team_alilm.application.port.out.ReadProcAlilmPort

@Service
@Transactional
class ReadMyAlilmService(
    val addAlilmPort: AddAlilmPort,
    val loadAlilmPort: LoadAlilmPort,
    val readProcAlilmPort: ReadProcAlilmPort
): MyAlilmReadUseCase, MyAlilmReadAllUseCase {

    override fun myAlilmRead(command: MyAlilmReadUseCase.MyAlilmReadCommand) {
        for (id in command.alilmIdList) {
            val alilm = loadAlilmPort.loadAlilmById(id)
            alilm.readAlilm()
            addAlilmPort.addAlilm(alilm)
        }
    }

    override fun myAlilmReadAll(command: MyAlilmReadAllUseCase.MyAlilmReadCommand) {
        readProcAlilmPort.readProcAlilm(command.member)
    }
}