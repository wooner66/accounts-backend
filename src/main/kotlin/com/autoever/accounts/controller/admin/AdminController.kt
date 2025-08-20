package com.autoever.accounts.controller.admin

import com.autoever.accounts.controller.admin.dto.GetUsersRequest
import com.autoever.accounts.controller.admin.dto.GetUsersResponse
import com.autoever.accounts.controller.admin.extension.toDto
import com.autoever.accounts.controller.admin.extension.toResponse
import com.autoever.accounts.service.admin.AdminService
import com.autoever.accounts.controller.admin.dto.UpdateUserRequest
import com.autoever.accounts.controller.admin.dto.UpdateUserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AdminController.BASE_URL)
class AdminController(
	private val adminService: AdminService,
) {
	companion object {
		const val BASE_URL = "/admin"
	}

	@GetMapping("/users")
	fun getUsers(@RequestParam request: GetUsersRequest): ResponseEntity<GetUsersResponse> {
		val response = adminService.getUsers(request.toDto())
		return ResponseEntity.ok(response.toResponse())
	}

	@PatchMapping("/users/{userId}")
	fun updateUser(@RequestBody request: UpdateUserRequest, @PathVariable userId: String): ResponseEntity<UpdateUserResponse> {
		val response = adminService.updateUser(request.toDto())
		return ResponseEntity.ok(response.toResponse())
	}

	@DeleteMapping("/users/{userId}")
	fun deleteUser(@PathVariable userId: String): ResponseEntity<Void> {
		try {
			adminService.deleteUser(userId.toLong())
		} catch (e: Exception) {
			return ResponseEntity.badRequest().build()
		}

		return ResponseEntity.noContent().build()
	}
}
