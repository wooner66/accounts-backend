package com.autoever.accounts.controller.admin.dto

data class GetUsersRequest(
    val page: Int?,
    val pageSize: Int?,
    val userIds: List<Long> = emptyList(),
    val username: String?,
    val name: String?,
    val phone: String?,
)
