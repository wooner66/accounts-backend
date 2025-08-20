package com.autoever.accounts.jpa.user.repository

import com.autoever.accounts.jpa.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserQueryDslRepository {
	fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByResidentRegistrationNumberHash(residentRegistrationNumberHash: String): Boolean
}
