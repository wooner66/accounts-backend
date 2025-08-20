package com.autoever.accounts.messaging

import com.autoever.accounts.external.ExternalMessagingClient
import com.autoever.accounts.jpa.job.SendLog
import com.autoever.accounts.jpa.job.SendLogRepository
import com.autoever.accounts.redis.RedisRateLimiter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AgeBucketConsumer(
	private val jdbcTemplate: JdbcTemplate,
	private val externalClient: ExternalMessagingClient,
	private val redisRateLimiter: RedisRateLimiter,
	private val sendLogRepository: SendLogRepository,
	@Value("\${app.external.kakaotalk.permitsPerMinute:100}") 
	private val kakaoPermits: Int,
	@Value("\${app.external.sms.permitsPerMinute:500}") 
	private val smsPermits: Int
) {
	private val logger = LoggerFactory.getLogger(AgeBucketConsumer::class.java)

	@KafkaListener(
		topics = ["\${app.messaging.kafka-topic:message-age-bucket}"], 
		groupId = "age-bucket-workers",
		concurrency = "3"
	)
	fun onMessage(payload: AgeBucketPayload) {
		logger.info("Received bucket job=${payload.jobId}, ageFrom=${payload.ageFrom}, ageTo=${payload.ageTo}")

		val pageSize = 1000
		var page = 0

		// 현재 연도로 연령대 계산
		val currentYear = LocalDate.now().year
		val fromYear = currentYear - payload.ageFrom
		val toYear = if (payload.ageTo != null) currentYear - payload.ageTo else null

		val year = currentYear % 100

		val baseWhereClause = if (toYear != null) {
			"""(CASE WHEN CAST(SUBSTRING(rrn_front,1,2) AS INT) > $year THEN 1900 + CAST(SUBSTRING(rrn_front,1,2) AS INT) ELSE 2000 + CAST(SUBSTRING(rrn_front,1,2) AS INT) END) BETWEEN $toYear AND $fromYear"""
		} else {
			// 90세 이상은 한 연령대로 본다 : birth_year <= fromYear
			"""(CASE WHEN CAST(SUBSTRING(rrn_front,1,2) AS INT) > $year THEN 1900 + CAST(SUBSTRING(rrn_front,1,2) AS INT) ELSE 2000 + CAST(SUBSTRING(rrn_front,1,2) AS INT) END) <= $fromYear"""
		}

		while (true) {
			val offset = page * pageSize
			val sql = """
                SELECT id, name, phone, rrn_front FROM users
                WHERE $baseWhereClause
                ORDER BY id
                LIMIT $pageSize OFFSET $offset
            """.trimIndent()

			val users = jdbcTemplate.queryForList(sql)

			if (users.isEmpty()) {
				break
			}

			for (row in users) {
				val id = (row["id"] as Number).toLong()
				val name = row["name"] as? String
				val phoneRaw = row["phone"] as? String
				val phone = phoneRaw?.filter { it.isDigit() } ?: continue
				val text = "${name ?: "회원"}님, 안녕하세요. 현대 오토에버입니다. ${payload.message}"

				val kakaoKey = "limiter:kakao:global"
				val gotKakao = redisRateLimiter.tryAcquire(
					key = kakaoKey,
					ratePerMin = kakaoPermits,
					capacity = kakaoPermits,
					requested = 1,
					timeoutMs = 2000,
				)
				if (!gotKakao) {
					sendLogRepository.save(
						SendLog(
							jobId = payload.jobId,
							userId = id,
							channel = "KAKAO",
							status = "RATE_LIMIT",
							response = null
						)
					)
					continue
				}

				val kakaoRes = externalClient.sendKakao(phone, text)
				if (kakaoRes.isSuccess) {
					sendLogRepository.save(
						SendLog(
							jobId = payload.jobId,
							userId = id,
							channel = "KAKAO",
							status = "OK",
							response = kakaoRes.body
						)
					)
				} else {
					// 카카오톡 실패 시 SMS로 대체 전송
					val smsKey = "limiter:sms:global"
					val gotSms = redisRateLimiter.tryAcquire(
						key = smsKey,
						ratePerMin = smsPermits,
						capacity = smsPermits,
						requested = 1,
						timeoutMs = 2000
					)
					if (!gotSms) {
						sendLogRepository.save(
							SendLog(
								jobId = payload.jobId,
								userId = id,
								channel = "SMS",
								status = "RATE_LIMIT",
								response = null
							)
						)
						continue
					}
					val smsRes = externalClient.sendSms(phone, text)
					sendLogRepository.save(
						SendLog(
							jobId = payload.jobId,
							userId = id,
							channel = "SMS",
							status = if (smsRes.isSuccess) "OK" else "FAILED",
							response = smsRes.body
						)
					)
				}
			}

			page += 1
		}
		logger.info("Completed bucket job=${payload.jobId}")
	}
}
