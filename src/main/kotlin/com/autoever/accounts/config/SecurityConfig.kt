package com.autoever.accounts.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
	private val jwtAuthFilter: JwtAuthFilter,
	@Value("\${app.admin.username}") private val adminUsername: String,
	@Value("\${app.admin.password}") private val adminPassword: String
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

	@Bean
	fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
		val admin = User.withUsername(adminUsername)
			.password(passwordEncoder.encode(adminPassword))
			.roles("ADMIN")
			.build()
		return InMemoryUserDetailsManager(admin)
	}

	@Bean
	fun filterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf { it.disable() }
			.headers { headers -> headers.frameOptions { it.disable() } }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests { auth ->
				auth.requestMatchers("/h2-console/**").permitAll()
				auth.requestMatchers("/api/users").permitAll()
				auth.requestMatchers("/api/login").permitAll()
				auth.requestMatchers("/admin/**").hasRole("ADMIN")
				auth.anyRequest().authenticated()
			}
			.httpBasic { }
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}

	@Bean
	fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
		return config.authenticationManager
	}
}
