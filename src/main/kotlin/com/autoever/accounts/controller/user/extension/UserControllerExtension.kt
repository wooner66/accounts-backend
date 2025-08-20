package com.autoever.accounts.controller.user.extension

import com.autoever.accounts.controller.user.dto.CreateUserResponse
import com.autoever.accounts.controller.user.dto.UserDetailResponse
import com.autoever.accounts.service.user.dto.CreateUserResponseDto
import com.autoever.accounts.service.user.dto.UserDto

fun CreateUserResponseDto.toResponse(): CreateUserResponse {
    return CreateUserResponse(
        isSuccess = isSuccess,
        message = message,
    )
}

fun UserDto.toResponse(): UserDetailResponse {
	return UserDetailResponse(
		username = username,
		name = name,
		phone = phone,
		topLevelAddress = topLevelAddress,
	)
}
