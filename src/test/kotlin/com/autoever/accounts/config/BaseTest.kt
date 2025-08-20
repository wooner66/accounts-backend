package com.autoever.accounts.config

import com.ninjasquad.springmockk.MockkBean
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BaseTest {
	@MockkBean
	lateinit var jwtAuthFilter: JwtAuthFilter
}