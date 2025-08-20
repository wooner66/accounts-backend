package com.autoever.accounts.service.admin.dto

data class UpdateUserRequestDto(
    val password: String?,
    val address: String?,
	val addressDetail: String?,
)
