package org.team_alilm.algamja.common.util

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig

/**
 * Jasypt 암호화 유틸리티
 * 
 * 사용 방법:
 * 1. 환경변수 설정:
 *    - JASYPT_PASSWORD: Jasypt 암호화 비밀번호
 *    - JASYPT_ALGORITHM: 암호화 알고리즘 (기본: PBEWithMD5AndDES)
 * 
 * 2. 실행:
 *    - 터미널에서: JASYPT_PASSWORD='your-password' JASYPT_ALGORITHM='PBEWithMD5AndDES' ./gradlew bootRun
 *    - IntelliJ에서: Run Configuration의 Environment Variables에 설정
 * 
 * 3. 암호화된 값 사용:
 *    - application.yml에서: ENC(암호화된값)
 * 
 * 주의: 이 유틸리티는 로컬에서만 사용하고, 실제 비밀번호는 절대 코드에 포함시키지 마세요!
 */
object JasyptEncryptUtil {
    
    fun main(args: Array<String>) {
        val password = System.getenv("JASYPT_PASSWORD") 
            ?: throw IllegalStateException("JASYPT_PASSWORD environment variable is required")
        val algorithm = System.getenv("JASYPT_ALGORITHM") ?: "PBEWithMD5AndDES"
        
        if (args.isEmpty()) {
            println("Usage: JasyptEncryptUtil <text-to-encrypt>")
            println("Environment variables required:")
            println("  JASYPT_PASSWORD: Encryption password")
            println("  JASYPT_ALGORITHM: Encryption algorithm (optional, default: PBEWithMD5AndDES)")
            return
        }
        
        val textToEncrypt = args[0]
        val encryptor = createEncryptor(password, algorithm)
        
        val encrypted = encryptor.encrypt(textToEncrypt)
        val decrypted = encryptor.decrypt(encrypted)
        
        println("========================================")
        println("Original: $textToEncrypt")
        println("Encrypted: ENC($encrypted)")
        println("Decrypted: $decrypted")
        println("========================================")
        println("\nAdd to application.yml:")
        println("  your.property: ENC($encrypted)")
    }
    
    private fun createEncryptor(password: String, algorithm: String): PooledPBEStringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()
        
        config.password = password
        config.algorithm = algorithm
        config.poolSize = 1
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        config.stringOutputType = "base64"
        
        encryptor.setConfig(config)
        return encryptor
    }
}