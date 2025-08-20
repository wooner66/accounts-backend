package com.autoever.accounts.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtUtil(
	@Value("\${app.jwt.secret}")
	private val secret: String,
	@Value("\${app.jwt.issuer}")
	private val issuer: String,
	@Value("\${app.jwt.expireMinutes}")
	private val expireMinutes: Long
) {
	private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8)) }

	fun generateToken(subject: String, claims: Map<String, Any> = emptyMap()): String {
		val now = Date()
		val exp = Date(now.time + expireMinutes * 60_000)
		return Jwts.builder()
			.setClaims(claims)
			.setSubject(subject)
			.setIssuer(issuer)
			.setIssuedAt(now)
			.setExpiration(exp)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact()
	}

	fun parseSubject(token: String): String? {
		return try {
			val parsed = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
			parsed.body.subject
		} catch (e: Exception) {
			null
		}
	}
}