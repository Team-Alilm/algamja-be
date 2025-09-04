package org.team_alilm.algamja.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
class FirebaseConfig {

    @Value("\${firebase.project-id:}")
    private val projectId: String = ""

    @Value("\${firebase.private-key-id:}")
    private val privateKeyId: String = ""

    @Value("\${firebase.private-key:}")
    private val privateKey: String = ""

    @Value("\${firebase.client-email:}")
    private val clientEmail: String = ""

    @Value("\${firebase.client-id:}")
    private val clientId: String = ""

    @Bean
    fun firebaseApp(): FirebaseApp {
        if (FirebaseApp.getApps().isEmpty()) {
            require(projectId.isNotBlank() && privateKey.isNotBlank()) {
                "Firebase configuration is missing. Please provide firebase configuration in application.yml"
            }
            
            // application.yml에서 설정을 읽어서 JSON 생성 (Jasypt로 복호화된 값 사용)
            val firebaseConfig = """
                {
                  "type": "service_account",
                  "project_id": "$projectId",
                  "private_key_id": "$privateKeyId",
                  "private_key": "$privateKey",
                  "client_email": "$clientEmail",
                  "client_id": "$clientId",
                  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                  "token_uri": "https://oauth2.googleapis.com/token",
                  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/${clientEmail.replace("@", "%40")}",
                  "universe_domain": "googleapis.com"
                }
            """.trimIndent()
            
            val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(firebaseConfig.toByteArray()))
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            return FirebaseApp.initializeApp(options)
        }
        return FirebaseApp.getInstance()
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}