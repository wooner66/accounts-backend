package com.autoever.accounts.controller.admin.dto

data class UpdateUserRequest(
    val id: Long,
    val password: String?,
    val address: String?,
)