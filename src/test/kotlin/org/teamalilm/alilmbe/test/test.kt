package org.teamalilm.alilmbe.test

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class Test {

    @Test
    fun test() {
        val restClient = RestClient.create()

        val response = restClient.get()
            .uri("https://www.musinsa.com/app/goods/4029596")
            .retrieve()
            .body<String>()

        println(response)
    }
}