package com.autoever.accounts.admin

import com.autoever.accounts.common.exception.NotFoundException
import com.autoever.accounts.config.BaseTest
import com.autoever.accounts.jpa.user.condition.UserSearchCondition
import com.autoever.accounts.jpa.user.repository.UserRepository
import com.autoever.accounts.service.admin.AdminService
import com.autoever.accounts.service.admin.dto.GetUsersRequestDto
import com.autoever.accounts.service.admin.dto.UpdateUserRequestDto
import com.autoever.accounts.user.mock.UserBuilder.`사용자 Entity 목록 생성`
import com.autoever.accounts.user.mock.UserBuilder.`사용자 Entity 생성`
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class AdminServiceTest : BaseTest() {
    lateinit var adminService: AdminService

    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean(relaxed = true)
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        adminService = AdminService(userRepository, passwordEncoder)
    }

    @Test
    fun `사용자 조회 테스트`() {
        // given
        val request = GetUsersRequestDto(
            userIds = listOf(1L, 2L),
            username = "kha1234",
            name = "김현오",
            phone = "7890",
            page = 0,
            pageSize = 50
        )

        val condition = UserSearchCondition(
            userIds = request.userIds,
            username = request.username,
            name = request.name,
            phone = request.phone
        )

        val pageable = PageRequest.of(
            request.page,
            request.pageSize
        )

        every {
            userRepository.findAllByCondition(condition, pageable)
        } returns PageImpl(`사용자 Entity 목록 생성`(), pageable, 2)

        // when
        val response = adminService.getUsers(request)

        // then
        with (response) {
            assertThat(users).isNotEmpty
        }
    }

    @Test
    fun `사용자 정보 수정 테스트 - 성공`() {
        // given
        val userId = 1L
        val request = UpdateUserRequestDto(
            id = userId,
            password = "newPassword123",
            address = "서울특별시 강남구 역삼동 123-45"
        )

        val userEntity = `사용자 Entity 목록 생성`().first()

        every {
            userRepository.findByIdOrNull(userId)
        } returns userEntity

        every {
            passwordEncoder.encode(request.password!!)
        } returns "encodedPassword123"

        // when
        val response = adminService.updateUser(request)

        // then
        assertThat(response.isSuccess).isTrue
    }

    @Test
    fun `사용자 정보 수정 테스트 - 실패`() {
        // given
        val userId = 1L
        val request = UpdateUserRequestDto(
            id = userId,
            password = "newPassword123",
            address = "서울특별시 강남구 역삼동 123-45"
        )

        every {
            userRepository.findByIdOrNull(userId)
        } returns null

        // when

        val response = adminService.updateUser(request)

        // then
        assertThat(response.isSuccess).isFalse
    }

    @Test
    fun `사용자 삭제 테스트 - 성공`() {
        // given
        val userId = 1L
        val user = `사용자 Entity 생성`()

        every {
            userRepository.findByIdOrNull(userId)
        } returns user

        every {
            userRepository.delete(user)
        } returns Unit

        // when
        val response = adminService.deleteUser(userId)

        // then
        assertThat(response).isTrue
    }

    @Test
    fun `사용자 삭제 테스트 - 실패`() {
        // given
        val userId = 1L

        every {
            userRepository.findByIdOrNull(userId)
        } returns null

        // when & then
        assertThrows<NotFoundException> {
            adminService.deleteUser(userId)
        }
    }
}
