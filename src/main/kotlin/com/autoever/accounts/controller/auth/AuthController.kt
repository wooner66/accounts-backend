package com.autoever.accounts.controller.auth

import com.autoever.accounts.controller.auth.dto.LoginRequest
import com.autoever.accounts.controller.auth.dto.LoginResponse
import com.autoever.accounts.service.auth.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AuthController.BASE_URL)
class AuthController(
	private val authService: AuthService,
) {
	companion object {
		const val BASE_URL = "/auth"
	}

	@PostMapping("/login")
	fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
		val token = authService.login(request.username, request.password)
		return ResponseEntity.ok(LoginResponse(token))
	}
}
