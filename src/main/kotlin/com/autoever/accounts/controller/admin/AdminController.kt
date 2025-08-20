package com.autoever.accounts.controller.admin

import com.autoever.accounts.controller.admin.dto.GetUsersRequest
import com.autoever.accounts.controller.admin.dto.GetUsersResponse
import com.autoever.accounts.controller.admin.dto.SendMessageRequest
import com.autoever.accounts.controller.admin.dto.SendMessageResponse
import com.autoever.accounts.controller.admin.dto.UpdateUserRequest
import com.autoever.accounts.controller.admin.dto.UpdateUserResponse
import com.autoever.accounts.controller.admin.extension.toDto
import com.autoever.accounts.controller.admin.extension.toResponse
import com.autoever.accounts.service.admin.AdminService
import com.autoever.accounts.service.messaging.MessagingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AdminController.BASE_URL)
class AdminController(
	private val adminService: AdminService,
	private val messagingService: MessagingService,
) {
	companion object {
		const val BASE_URL = "/admin"
	}

	@GetMapping("/users")
	fun getUsers(@ModelAttribute request: GetUsersRequest): ResponseEntity<GetUsersResponse> {
		val response = adminService.getUsers(request.toDto())
		return ResponseEntity.ok(response.toResponse())
	}

	@PatchMapping("/users/{userId}")
	fun updateUser(@RequestBody request: UpdateUserRequest, @PathVariable userId: String): ResponseEntity<UpdateUserResponse> {
		val response = adminService.updateUser(userId.toLong(), request.toDto())
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

	@PostMapping("/send_message/all")
	fun sendMessageToAllUsers(@RequestBody request: SendMessageRequest): ResponseEntity<SendMessageResponse> {
		return try {
			messagingService.sendMessageToAllUsers(request.message, request.age)
			ResponseEntity.ok(
				SendMessageResponse(
					message = "OK"
				)
			)
		} catch (e: Exception) {
			ResponseEntity.ok(
				SendMessageResponse(
					message = e.message ?: "메시지 전송 요청 처리 중 오류가 발생했습니다."
				)
			)
		}
	}
}
