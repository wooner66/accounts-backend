package com.autoever.accounts.controller.admin.dto

data class UpdateUserRequest(
    val password: String?,
    val address: String?,
	val addressDetail: String?,
)