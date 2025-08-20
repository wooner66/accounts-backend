package com.autoever.accounts.service.messaging

import com.autoever.accounts.jpa.job.MessageJob
import com.autoever.accounts.jpa.job.MessageJobRepository
import com.autoever.accounts.messaging.AgeBucketPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessagingService(
	private val jobRepository: MessageJobRepository,
	private val kafkaTemplate: KafkaTemplate<String, Any>,
	@Value("\${app.messaging.kafka-topic:message-age-bucket}")
	private val topic: String
) {

	@Transactional
	fun sendMessageToAllUsers(message: String, age: Int) {
		// validate age
		val validAges = (0..9).map { it * 10 } + 90
		if (age !in validAges) {
			throw IllegalArgumentException("age는 0,10,20,...,90 중 하나여야 합니다.")
		}

		val job = MessageJob(
			requestedBy = "admin",
			message = message,
			status = "PENDING",
			totalTargets = 0L,
		)
		jobRepository.save(job)

		val to = if (age == 90) null else age + 9

		val payload = AgeBucketPayload(
			jobId = job.id!!,
			ageFrom = age,
			ageTo = to,
			message = message
		)

		kafkaTemplate.send(topic, payload)
		job.updateStatus("PENDING")
		jobRepository.save(job)
	}
}
