package com.autoever.accounts.util.crypto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class Crypto(
    @Value("\${app.crypto.secret-key}")
	private val secretKey: String,
    @Value("\${app.crypto.iv}")
	private val iv: String,
) {
	companion object {
		private const val ALGORITHM = "AES"
		private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
		private const val KEY_LENGTH = 32
		private const val IV_LENGTH = 16
		private const val ENCODING = "UTF-8"
	}

	private val secretKeySpec = makeSecretKeySpec(secretKey)
	private val ivParameterSpec = makeIvSpec(iv)
	private val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)

	fun encryptAndEncode(rawData: Any): String {
		return URLEncoder.encode(encrypt(rawData), ENCODING)
	}

	fun decodeAndDecrypt(encryptedData: String): String {
		return this.decrypt(URLDecoder.decode(encryptedData, ENCODING))
	}

	fun encrypt(rawData: Any): String {
		this.setCipherMode(Cipher.ENCRYPT_MODE)

		val rawStr = rawData.toString()

		this.cipher.doFinal(rawStr.toByteArray()).also {
			return Base64.getUrlEncoder().encodeToString(it)
		}
	}

	fun decrypt(encryptedData: String): String {
		this.setCipherMode(Cipher.DECRYPT_MODE)

		return try {
			String(this.cipher.doFinal(Base64.getUrlDecoder().decode(encryptedData)), StandardCharsets.UTF_8)
		} catch (e: IllegalBlockSizeException) {
			""
		}
	}

	private fun makeIvSpec(iv: String): IvParameterSpec {
		return IvParameterSpec(iv.toByteArray().copyOf(IV_LENGTH))
	}

	private fun makeSecretKeySpec(secretKey: String): SecretKeySpec {
		return SecretKeySpec(secretKey.toByteArray().copyOf(KEY_LENGTH), ALGORITHM)
	}

	private fun setCipherMode(opMode: Int) {
		this.cipher.init(opMode, this.secretKeySpec, this.ivParameterSpec)
	}
}
