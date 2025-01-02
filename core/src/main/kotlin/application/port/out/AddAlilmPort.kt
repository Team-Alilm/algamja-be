package org.team_alilm.application.port.out

import domain.Alilm

interface AddAlilmPort {

    fun addAlilm(alilm: Alilm)
}