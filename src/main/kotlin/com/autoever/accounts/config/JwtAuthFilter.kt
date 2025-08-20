package com.autoever.accounts.config

import com.autoever.accounts.jpa.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
	private val jwtUtil: JwtUtil,
	private val userRepository: UserRepository,
) : OncePerRequestFilter() {

	override fun shouldNotFilter(request: HttpServletRequest): Boolean {
		val path = request.servletPath
		return path.startsWith("/admin/") ||
				path.startsWith("/h2-console") ||
				(path == "/auth/login" && request.method == "POST") ||
				(path == "/users" && request.method == "POST")
	}

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		val authHeader = request.getHeader("Authorization")
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			val token = authHeader.substring(7)
			val username = jwtUtil.parseSubject(token)
			if (username != null && SecurityContextHolder.getContext().authentication == null) {
				val user = userRepository.findByUsername(username)
				if (user != null) {
					val auth = UsernamePasswordAuthenticationToken(
						user.username, null, listOf(SimpleGrantedAuthority("ROLE_USER"))
					)
					auth.details = WebAuthenticationDetailsSource().buildDetails(request)
					SecurityContextHolder.getContext().authentication = auth
				}
			}
		}
		filterChain.doFilter(request, response)
	}
}
