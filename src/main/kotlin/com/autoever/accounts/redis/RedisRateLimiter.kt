package com.autoever.accounts.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class RedisRateLimiter(
	private val redisTemplate: StringRedisTemplate
) {
	// Lua token-bucket 스크립트 (원자적)
	private val luaScript = """
        local key = KEYS[1]
        local now = tonumber(ARGV[1])
        local ratePerMin = tonumber(ARGV[2])
        local capacity = tonumber(ARGV[3])
        local requested = tonumber(ARGV[4])
        local data = redis.call("HMGET", key, "tokens", "last")
        local tokens = tonumber(data[1]) or capacity
        local last = tonumber(data[2]) or 0
        local elapsed = math.max(0, now - last)
        local refill = (elapsed / 60000) * ratePerMin
        tokens = math.min(capacity, tokens + refill)
        if tokens < requested then
          return 0
        else
          tokens = tokens - requested
          redis.call("HMSET", key, "tokens", tokens, "last", now)
          redis.call("EXPIRE", key, 120)
          return 1
        end
    """.trimIndent()

	private val script = DefaultRedisScript<Long>(luaScript, Long::class.java)

	fun tryAcquire(
		key: String,
		ratePerMin: Int,
		capacity: Int = ratePerMin,
		requested: Int = 1,
		timeoutMs: Long = 2000L
	): Boolean {
		val start = System.currentTimeMillis()
		while (System.currentTimeMillis() - start < timeoutMs) {
			val now = Instant.now().toEpochMilli().toString()
			val res = redisTemplate.execute(script, listOf(key), now, ratePerMin.toString(), capacity.toString(), requested.toString())
			if (res == 1L) return true
			Thread.sleep(10)
		}
		return false
	}
}
