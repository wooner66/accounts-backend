package com.autoever.accounts.service.auth

import com.autoever.accounts.common.exception.NotFoundException
import com.autoever.accounts.config.JwtUtil
import com.autoever.accounts.jpa.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtUtil: JwtUtil,
) {
	fun login(username: String, rawPassword: String): String {
		val user = userRepository.findByUsername(username)
			?: throw NotFoundException("사용자 조회에 실패했습니다: $username")

		if (!passwordEncoder.matches(rawPassword, user.password)) {
			throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
		}

		return jwtUtil.generateToken(user.username, mapOf("uid" to (user.id ?: -1)))
	}
}
