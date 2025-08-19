package com.autoever.accounts.controller.user.extension

import com.autoever.accounts.controller.user.dto.CreateUserResponse
import com.autoever.accounts.service.user.dto.CreateUserResponseDto

fun CreateUserResponseDto.toResponse(): CreateUserResponse {
    return CreateUserResponse(
        isSuccess = isSuccess,
        message = message,
    )
}
