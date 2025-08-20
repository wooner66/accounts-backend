package com.autoever.accounts.controller.admin.dto

import com.autoever.accounts.service.user.dto.UserDto

data class GetUsersResponse(
    val users: List<UserDto>,
    val totalCount: Long,
    val page: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val totalPages: Int,
)
