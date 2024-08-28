package org.teamalilm.alilm.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource


@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        val firebaseSecretKey = ClassPathResource("/firebase/FirebaseSecretKey.json")
        val firebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(firebaseSecretKey.inputStream))
            .build()

        return FirebaseApp.initializeApp(firebaseOptions)
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp?): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}