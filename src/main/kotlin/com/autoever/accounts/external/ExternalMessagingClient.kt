package com.autoever.accounts.external

import com.autoever.accounts.external.dto.SendResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class ExternalMessagingClient(
	private val rest: RestTemplate,
	@Value("\${app.external.kakaotalk.url}")
	private val kakaoUrl: String,
	@Value("\${app.external.kakaotalk.username}")
	private val kakaoUser: String,
	@Value("\${app.external.kakaotalk.password}")
	private val kakaoPass: String,
	@Value("\${app.external.sms.url}")
	private val smsUrl: String,
	@Value("\${app.external.sms.username}")
	private val smsUser: String,
	@Value("\${app.external.sms.password}")
	private val smsPass: String,
) {
	fun sendKakao(phone: String, message: String): SendResult {
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_JSON
		headers.setBasicAuth(kakaoUser, kakaoPass)
		val payload = mapOf("phone" to phone, "message" to message)
		val entity = HttpEntity(payload, headers)
		return try {
			val response = rest.exchange(kakaoUrl, HttpMethod.POST, entity, String::class.java)
			SendResult(response.statusCode.is2xxSuccessful, response.statusCode.value(), response.body)
		} catch (ex: Exception) {
			SendResult(false, 500, ex.message)
		}
	}

	fun sendSms(phone: String, message: String): SendResult {
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
		headers.setBasicAuth(smsUser, smsPass)
		val form: MultiValueMap<String, String> = LinkedMultiValueMap()
		form.add("message", message)

		val url = "$smsUrl?phone=${phone}"
		val entity = HttpEntity(form, headers)
		return try {
			val resp = rest.postForEntity(url, entity, String::class.java)
			SendResult(resp.statusCode.is2xxSuccessful, resp.statusCode.value(), resp.body)
		} catch (ex: Exception) {
			SendResult(false, 500, ex.message)
		}
	}
}
