package com.autoever.accounts.util.crypto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class Crypto(
    @Value("\${app.crypto.aesKeyBase64}") private val keyBase64: String
) {
    private val secureRandom = SecureRandom()
    private val key: SecretKey by lazy {
        val raw = Base64.getDecoder().decode(keyBase64) // 16/24/32 bytes
        SecretKeySpec(raw, "AES")
    }

    fun encryptAesGcm(plain: String): String {
        val iv = ByteArray(12).also { secureRandom.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val cipherText = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val packed = iv + cipherText
        return Base64.getEncoder().encodeToString(packed)
    }

    fun decryptAesGcm(packedBase64: String): String {
        val packed = Base64.getDecoder().decode(packedBase64)
        val iv = packed.copyOfRange(0, 12)
        val cipherText = packed.copyOfRange(12, packed.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        val plain = cipher.doFinal(cipherText)
        return String(plain, Charsets.UTF_8)
    }
}