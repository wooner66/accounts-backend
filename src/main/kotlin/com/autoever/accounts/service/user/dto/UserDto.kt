package com.autoever.accounts.service.user.dto

data class UserDto(
    val id: Long,
    val username: String,
    val name: String,
    val phone: String,
    val topLevelAddress: String,
)
