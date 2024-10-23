package org.team_alilm.application.port.out

import org.team_alilm.domain.Alilm

interface AddAlilmPort {

    fun addAlilm(alilm: Alilm)
}