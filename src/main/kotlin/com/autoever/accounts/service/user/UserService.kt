package com.autoever.accounts.service.user

import com.autoever.accounts.jpa.user.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
)