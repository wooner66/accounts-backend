package com.autoever.accounts.service.user

import com.autoever.accounts.jpa.user.UserRepository
import com.autoever.accounts.service.user.dto.CreateUserRequest
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun createUser(request: CreateUserRequest) {
        val user = request.toEntity()
        userRepository.save(user)
    }
}
