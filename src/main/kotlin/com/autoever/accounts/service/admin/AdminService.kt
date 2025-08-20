package com.autoever.accounts.service.admin

import com.autoever.accounts.common.exception.NotFoundException
import com.autoever.accounts.common.extension.getTopLevelByAddress
import com.autoever.accounts.jpa.user.User
import com.autoever.accounts.jpa.user.condition.UserSearchCondition
import com.autoever.accounts.jpa.user.repository.UserRepository
import com.autoever.accounts.service.admin.dto.GetUsersRequestDto
import com.autoever.accounts.service.admin.dto.GetUsersResponseDto
import com.autoever.accounts.service.admin.dto.UpdateUserRequestDto
import com.autoever.accounts.service.admin.dto.UpdateUserResponseDto
import com.autoever.accounts.service.user.dto.UserDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
) {

	@Transactional(readOnly = true)
	fun getUsers(request: GetUsersRequestDto): GetUsersResponseDto {
		val condition = UserSearchCondition(
			userIds = request.userIds,
			username = request.username,
			name = request.name,
			phone = request.phone
		)

		val pageable = PageRequest.of(
			request.page,
			request.pageSize,
		)

		val users = userRepository.findAllByCondition(condition, pageable)
		val userDtos = users.content.mapNotNull { user ->
			if (user.id == null) {
				return@mapNotNull null
			}

			UserDto(
				id = user.id!!,
				username = user.username,
				name = user.name,
				phone = user.phone,
				topLevelAddress = user.topLevelAddress,
			)
		}

		return GetUsersResponseDto(
			users = userDtos,
			totalCount = users.totalElements,
			page = request.page,
			isFirst = users.isFirst,
			isLast = users.isLast,
			totalPages = users.totalPages,
		)
	}

	@Transactional
	fun updateUser(request: UpdateUserRequestDto): UpdateUserResponseDto {
		val user = userRepository.findByIdOrNull(request.id)
			?: return UpdateUserResponseDto(
				isSuccess = false,
				message = "사용자를 찾을 수 없습니다. : 사용자 ID = ${request.id}"
			)

		request.password?.let { password ->
			user.updatePassword(passwordEncoder.encode(password))
		}

		request.address?.let { address ->
			user.updateTopLevelAddress(address.getTopLevelByAddress())
			user.updateAddress(address)
		}

		return UpdateUserResponseDto(
			isSuccess = true,
			message = "사용자 정보가 성공적으로 업데이트되었습니다.",
		)
	}

	@Transactional
	fun deleteUser(userId: Long): Boolean {
		val user = userRepository.findByIdOrNull(userId)
			?: throw NotFoundException("사용자를 찾을 수 없습니다. : 사용자 ID = $userId")

		userRepository.delete(user)
		return true
	}
}
