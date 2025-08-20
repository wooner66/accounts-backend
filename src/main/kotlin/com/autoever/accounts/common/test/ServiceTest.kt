package com.autoever.accounts.common.test

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@DefaultTest
@ExtendWith(SpringExtension::class, MockKExtension::class)
annotation class ServiceTest
