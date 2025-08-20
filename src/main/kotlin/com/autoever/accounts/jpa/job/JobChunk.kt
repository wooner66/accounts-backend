package com.autoever.accounts.jpa.job

import com.autoever.accounts.jpa.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "job_chunks")
class JobChunk(
	var jobId: Long,
	var startId: Long,
	var endId: Long,
	var chunkSize: Int = 1000,
	var status: String = "PENDING",
	var workerId: String? = null,
	var error: String? = null,
) : BaseEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as JobChunk

		if (jobId != other.jobId) return false
		if (startId != other.startId) return false
		if (endId != other.endId) return false
		if (chunkSize != other.chunkSize) return false
		if (status != other.status) return false
		if (workerId != other.workerId) return false
		if (error != other.error) return false

		return true
	}

	override fun hashCode(): Int = javaClass.hashCode()
}
