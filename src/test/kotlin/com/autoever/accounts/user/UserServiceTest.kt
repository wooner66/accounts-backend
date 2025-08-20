package com.autoever.accounts.user

import com.autoever.accounts.common.exception.DuplicatedUserException
import com.autoever.accounts.common.test.ServiceTest
import com.autoever.accounts.config.BaseTest
import com.autoever.accounts.jpa.user.repository.UserRepository
import com.autoever.accounts.service.user.UserService
import com.autoever.accounts.service.user.dto.CreateUserRequestDto
import com.autoever.accounts.user.mock.UserBuilder.`사용자 Entity 생성`
import com.autoever.accounts.util.crypto.Crypto
import com.autoever.accounts.util.hash.Hashing
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

@ServiceTest
class UserServiceTest : BaseTest() {
    lateinit var userService: UserService

    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean(relaxed = true)
    lateinit var encoder: PasswordEncoder

    @MockkBean
    lateinit var crypto: Crypto

    @MockkBean
    lateinit var hashing: Hashing

    @BeforeEach
    fun setUp() {
        userService = UserService(
            userRepository = userRepository,
            encoder = encoder,
            crypto = crypto,
            hashing = hashing
        )
    }

    @Test
    fun `사용자 생성 - 성공`() {
        // given
        val request = CreateUserRequestDto(
            username = "testuser",
            password = "password123",
            name = "Test User",
            residentRegistrationNumber = "123456-1234567",
            phoneNumber = "010-1234-5678",
            address = "서울시",
            addressDetail = "강남구 대치동"
        )

        `공통 검증 로직`(
            isExistingUser = false,
            isExistingResidentRegistrationNumber = false,
            createUserRequestDto = request
        )

        // when
        val response = userService.createUser(request = request)

        // then
        with(response) {
            Assertions.assertThat(isSuccess).isTrue()
        }
    }

    @Test
    fun `사용자 생성 - 실패 - 중복된 사용자명`() {
        // given
        val request = CreateUserRequestDto(
            username = "existinguser",
            password = "password123",
            name = "Existing User",
            residentRegistrationNumber = "123456-1234567",
            phoneNumber = "010-1234-5678",
            address = "서울시",
            addressDetail = "강남구 대치동"
        )

        `공통 검증 로직`(
            isExistingUser = true,
            isExistingResidentRegistrationNumber = false,
            createUserRequestDto = request
        )

        assertThrows<DuplicatedUserException> { userService.createUser(request) }
    }

    @Test
    fun `사용자 생성 - 실패 - 중복된 주민등록번호`() {
        // given
        val request = CreateUserRequestDto(
            username = "testuser",
            password = "password123",
            name = "Test User",
            residentRegistrationNumber = "123456-1234567",
            phoneNumber = "010-1234-5678",
            address = "서울시",
            addressDetail = "강남구 대치동"
        )

        `공통 검증 로직`(
            isExistingUser = false,
            isExistingResidentRegistrationNumber = true,
            createUserRequestDto = request
        )

        // when & then
        assertThrows<DuplicatedUserException> { userService.createUser(request) }
    }

    @Test
    fun `사용자 생성 - 실패 - 잘못된 주민등록번호`() {
        // given
        val request = CreateUserRequestDto(
            username = "testuser",
            password = "password123",
            name = "Test User",
            residentRegistrationNumber = "invalid-rrn",
            phoneNumber = "010-1234-5678",
            address = "서울시",
            addressDetail = "강남구 대치동"
        )

        `공통 검증 로직`(
            isExistingUser = false,
            isExistingResidentRegistrationNumber = false,
            createUserRequestDto = request
        )

        // when & then
        assertThrows<IllegalArgumentException> { userService.createUser(request) }
    }

    @Test
    fun `사용자 생성 - 실패 - 잘못된 핸드폰 번호`() {
        // given
        val request = CreateUserRequestDto(
            username = "testuser",
            password = "password123",
            name = "Test User",
            residentRegistrationNumber = "123456-1234567",
            phoneNumber = "invalid-phone",
            address = "서울시",
            addressDetail = "강남구 대치동"
        )

        `공통 검증 로직`(
            isExistingUser = false,
            isExistingResidentRegistrationNumber = false,
            createUserRequestDto = request
        )

        // when & then
        assertThrows<IllegalArgumentException> { userService.createUser(request) }
    }

    private fun `공통 검증 로직`(
        isExistingUser: Boolean = false,
        isExistingResidentRegistrationNumber: Boolean = false,
        createUserRequestDto: CreateUserRequestDto
    ) {
        every {
            hashing.sha256Hex(any())
        } returns "hashedValue"

        every {
            userRepository.existsByUsername(any())
        } returns isExistingUser

        every {
            userRepository.existsByResidentRegistrationNumberHash(any())
        } returns isExistingResidentRegistrationNumber

        every {
            crypto.encryptAndEncode(any())
        } returns "encryptedValue"

        every {
            encoder.encode(any())
        } returns "encodedPassword"

        every {
            userRepository.save(any())
        } returns `사용자 Entity 생성`(createUserRequestDto)
            .apply {
                id = 1L
            }
    }
}
