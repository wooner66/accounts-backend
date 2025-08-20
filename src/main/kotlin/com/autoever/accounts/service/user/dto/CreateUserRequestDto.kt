package com.autoever.accounts.service.user.dto

data class CreateUserRequestDto(
    val username: String,
    val password: String,
    val name: String,
    val residentRegistrationNumber: String,
    val phoneNumber: String,
    val address: String,
    val addressDetail: String,
)
