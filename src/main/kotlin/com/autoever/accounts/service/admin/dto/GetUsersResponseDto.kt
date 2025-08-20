package com.autoever.accounts.service.admin.dto

import com.autoever.accounts.service.user.dto.UserDto

data class GetUsersResponseDto(
    val users: List<UserDto>,
    val totalCount: Long,
    val page: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val totalPages: Int,
)
