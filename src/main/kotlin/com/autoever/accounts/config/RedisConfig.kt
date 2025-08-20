package com.autoever.accounts.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig {

	@Bean
	fun lettuceConnectionFactory(): LettuceConnectionFactory {
		// 기본 생성자는 application.properties / yaml 의 spring.redis.* 값을 사용
		return LettuceConnectionFactory()
	}

	@Bean
	fun stringRedisTemplate(): StringRedisTemplate {
		val tpl = StringRedisTemplate()
		tpl.connectionFactory = lettuceConnectionFactory()
		return tpl
	}
}