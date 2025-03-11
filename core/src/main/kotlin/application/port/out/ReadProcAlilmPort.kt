package org.team_alilm.application.port.out

import domain.Alilm
import domain.Member

interface ReadProcAlilmPort {

    fun readProcAlilm(member: Member)
}