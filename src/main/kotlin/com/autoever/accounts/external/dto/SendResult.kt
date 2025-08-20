package com.autoever.accounts.external.dto

data class SendResult(
	val isSuccess: Boolean,
	var status: Int,
	val body: String?,
)
