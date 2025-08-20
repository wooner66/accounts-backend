package com.autoever.accounts.service.admin.dto

data class UpdateUserRequestDto(
    val id: Long,
    val password: String?,
    val address: String?,
)
