package com.autoever.accounts.jpa.job

import com.autoever.accounts.jpa.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "send_logs")
class SendLog(
	var jobId: Long,
	var userId: Long,
	var channel: String,
	var status: String,
	@Column(columnDefinition = "text")
	var response: String? = null,
) : BaseEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as SendLog

		if (jobId != other.jobId) return false
		if (userId != other.userId) return false
		if (channel != other.channel) return false
		if (status != other.status) return false
		if (response != other.response) return false

		return true
	}

	override fun hashCode(): Int = javaClass.hashCode()
}
