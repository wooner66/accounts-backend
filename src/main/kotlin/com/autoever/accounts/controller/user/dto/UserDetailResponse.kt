package com.autoever.accounts.controller.user.dto

data class UserDetailResponse(
	val username: String,
	val name: String,
	val phone: String,
	val topLevelAddress: String,
)