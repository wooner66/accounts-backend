package com.autoever.accounts.controller.admin.extension

import com.autoever.accounts.controller.admin.dto.GetUsersRequest
import com.autoever.accounts.controller.admin.dto.GetUsersResponse
import com.autoever.accounts.controller.admin.dto.SendMessageResponse
import com.autoever.accounts.controller.admin.dto.UpdateUserRequest
import com.autoever.accounts.controller.admin.dto.UpdateUserResponse
import com.autoever.accounts.service.admin.dto.GetUsersRequestDto
import com.autoever.accounts.service.admin.dto.GetUsersResponseDto
import com.autoever.accounts.service.admin.dto.SendMessageResponseDto
import com.autoever.accounts.service.admin.dto.UpdateUserRequestDto
import com.autoever.accounts.service.admin.dto.UpdateUserResponseDto
import kotlin.Long

fun GetUsersRequest.toDto(): GetUsersRequestDto {
    return GetUsersRequestDto(
        page = page ?: 0,
        pageSize = pageSize ?: 50,
        userIds = userIds,
        username = username,
        name = name,
        phone = phone
    )
}

fun GetUsersResponseDto.toResponse(): GetUsersResponse {
    return GetUsersResponse(
        users = users,
        totalCount = totalCount,
        page = page,
        isFirst = isFirst,
        isLast = isLast,
        totalPages = totalPages
    )
}

fun UpdateUserRequest.toDto(): UpdateUserRequestDto {
    return UpdateUserRequestDto(
        id = id,
        password = password,
        address = address,
    )
}

fun UpdateUserResponseDto.toResponse(): UpdateUserResponse {
    return UpdateUserResponse(
        isSuccess = isSuccess,
        message = message,
    )
}

fun SendMessageResponseDto.toResponse(): SendMessageResponse {
	return SendMessageResponse(
		isSuccess = isSuccess,
		message = message,
	)
}
