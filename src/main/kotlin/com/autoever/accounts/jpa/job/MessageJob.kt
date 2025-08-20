package com.autoever.accounts.jpa.job

import com.autoever.accounts.jpa.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "message_jobs")
class MessageJob(
	var requestedBy: String? = null,
	@Column(columnDefinition = "text")
	var message: String,

	var status: String = "PENDING",
	var totalTargets: Long = 0L,
) : BaseEntity() {
	fun updateStatus(newStatus: String) {
		this.status = newStatus
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MessageJob

		if (totalTargets != other.totalTargets) return false
		if (requestedBy != other.requestedBy) return false
		if (message != other.message) return false
		if (status != other.status) return false

		return true
	}

	override fun hashCode(): Int = javaClass.hashCode()
}
