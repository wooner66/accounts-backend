package com.autoever.accounts.controller.user

import com.autoever.accounts.controller.user.dto.CreateUserRequest
import com.autoever.accounts.controller.user.dto.CreateUserResponse
import com.autoever.accounts.controller.user.extension.toResponse
import com.autoever.accounts.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UserController.BASE_URL)
class UserController(
    private val userService: UserService,
) {
    companion object {
        const val BASE_URL = "/users"
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<CreateUserResponse> {
        val response = userService.createUser(request.toDto()).toResponse()
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
    }
}