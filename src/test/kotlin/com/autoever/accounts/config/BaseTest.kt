package com.autoever.accounts.config

import com.autoever.accounts.messaging.AgeBucketConsumer
import com.autoever.accounts.redis.RedisRateLimiter
import com.ninjasquad.springmockk.MockkBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.client.RestTemplate

@SpringBootTest
class BaseTest {
	@MockkBean
	lateinit var jwtAuthFilter: JwtAuthFilter

	@MockkBean
	lateinit var jwtUtil: JwtUtil

	@MockkBean
	lateinit var ageBucketConsumer: AgeBucketConsumer

	@MockkBean
	lateinit var redisRateLimiter: RedisRateLimiter
}