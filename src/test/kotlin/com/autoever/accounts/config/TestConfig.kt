package com.autoever.accounts.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.client.RestTemplate

@TestConfiguration
class TestConfig {

	@Bean
	@Primary
	fun testPasswordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	@Bean
	@Primary
	fun testUserDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
		val adminRaw = "1212"
		val adminEncoded = passwordEncoder.encode(adminRaw)
		val admin = User.withUsername("admin")
			.password(adminEncoded)
			.roles("ADMIN")
			.build()
		return InMemoryUserDetailsManager(admin)
	}

	@Bean
	@Primary
	fun testLettuceConnectionFactory(): LettuceConnectionFactory {
		return LettuceConnectionFactory()
	}

	@Bean
	@Primary
	fun testStringRedisTemplate(): StringRedisTemplate {
		val tpl = StringRedisTemplate()
		tpl.connectionFactory = testLettuceConnectionFactory()
		return tpl
	}

	@Bean
	@Primary
	fun testRestTemplate(): RestTemplate {
		return RestTemplate()
	}
}