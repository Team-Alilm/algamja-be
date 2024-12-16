package org.team_alilm.global.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true) // Getter 명명 규칙 수정
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        return mapper
    }
}
