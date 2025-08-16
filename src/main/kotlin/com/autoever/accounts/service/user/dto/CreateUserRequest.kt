package com.autoever.accounts.service.user.dto

import com.autoever.accounts.jpa.user.User

data class CreateUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val residentRegistrationNumber: String,
    val phoneNumber: String,
    val address: String,
    val addressDetail: String,
) {
    fun toEntity(): User {
        return User(
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
