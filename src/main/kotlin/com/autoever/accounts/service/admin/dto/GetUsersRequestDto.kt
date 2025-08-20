package com.autoever.accounts.service.admin.dto

data class GetUsersRequestDto(
    val page: Int = 0,
    val pageSize: Int = 50,
    val userIds: List<Long>,
    val username: String?,
    val name: String?,
    val phone: String?,
)
