package com.autoever.accounts.jpa.user.condition

data class UserSearchCondition(
    val userIds: List<Long>,
    val username: String?,
    val name: String?,
    val phone: String?,
)
