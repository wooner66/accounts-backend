package com.autoever.accounts.controller.user.dto

import com.autoever.accounts.service.user.dto.CreateUserRequestDto

data class CreateUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val residentRegistrationNumber: String,
    val phoneNumber: String,
    val address: String,
    val addressDetail: String
) {
    fun toDto(): CreateUserRequestDto {
        return CreateUserRequestDto(
            username = username,
            password = password,
            name = name,
            residentRegistrationNumber = residentRegistrationNumber,
            phoneNumber = phoneNumber,
            address = address,
            addressDetail = addressDetail
        )
    }
}
