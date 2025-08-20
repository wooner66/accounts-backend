package com.autoever.accounts.service.user

import com.autoever.accounts.common.exception.DuplicatedUserException
import com.autoever.accounts.common.exception.NotFoundException
import com.autoever.accounts.common.extension.getTopLevelByAddress
import com.autoever.accounts.jpa.user.User
import com.autoever.accounts.jpa.user.repository.UserRepository
import com.autoever.accounts.service.user.dto.CreateUserRequestDto
import com.autoever.accounts.service.user.dto.CreateUserResponseDto
import com.autoever.accounts.service.user.dto.UserDto
import com.autoever.accounts.util.crypto.Crypto
import com.autoever.accounts.util.hash.Hashing
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val crypto: Crypto,
    private val hashing: Hashing,
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUser(request: CreateUserRequestDto): CreateUserResponseDto {
        val residentRegistrationNumberDigits = request.residentRegistrationNumber.filter { it.isDigit() }
        val residentRegistrationNumberHash = hashing.sha256Hex(residentRegistrationNumberDigits)

        validateCreateUserRequest(
            username = request.username,
            residentRegistrationNumberDigits = residentRegistrationNumberDigits,
            phoneNumber = request.phoneNumber,
            residentRegistrationNumberHash = residentRegistrationNumberHash
        )

        val residentRegistrationNumberFront = residentRegistrationNumberDigits.take(6)
        val residentRegistrationNumberBack = residentRegistrationNumberDigits.substring(6, 13)
        val residentRegistrationNumberBackEnc = crypto.encryptAndEncode(residentRegistrationNumberBack)
        val passwordHash = encoder.encode(request.password)
        val topLevelAddress = request.address.getTopLevelByAddress()

        val user = try {
            userRepository.save(
                User(
                    username = request.username,
                    password = passwordHash,
                    name = request.name,
                    residentRegistrationNumberFront = residentRegistrationNumberFront,
                    residentRegistrationNumberBack = residentRegistrationNumberBackEnc,
                    residentRegistrationNumberHash = residentRegistrationNumberHash,
                    phone = request.phoneNumber,
                    topLevelAddress = topLevelAddress,
                    addressDetail = "${request.address} ${request.addressDetail}".trim()
                )
            )
        } catch (e: Exception) {
            logger.error("회원 가입 실패: ${e.message}", e)
            return CreateUserResponseDto(
                isSuccess = false,
                message = "회원 가입에 실패했습니다. 관리자에게 문의해주세요. : ${e.message}"
            )
        }

        if (user.id == null) {
            logger.error("회원 가입 실패: 사용자 저장에 실패했습니다.")
            return CreateUserResponseDto(
                isSuccess = false,
                message = "회원 가입에 실패했습니다. 관리자에게 문의해주세요."
            )
        }

        return CreateUserResponseDto(
            isSuccess = true,
            message = "회원 가입에 성공했습니다."
        )
    }

    /**
     * 사용자 생성 요청을 검증합니다.
     * - 주민등록번호는 13자리여야 합니다.
     * - 핸드폰 번호는 11자리 이상이어야 합니다.
     * - 사용자 이름은 중복되지 않아야 합니다.
     * - 주민등록번호 해시값은 중복되지 않아야 합니다.
     */
    private fun validateCreateUserRequest(
        username: String,
        residentRegistrationNumberDigits: String,
        phoneNumber: String,
        residentRegistrationNumberHash: String,
    ) {
        require(residentRegistrationNumberDigits.length == 13) { "주민등록번호는 13자리여야 합니다." }
        require(phoneNumber.matches(Regex("""^\d{3}-\d{4}-\d{4}$"""))) { "핸드폰 번호는 11자리여야 합니다." }

        if (userRepository.existsByUsername(username)) {
            throw DuplicatedUserException("이미 가입된 계정(ID)입니다.")
        }

        if (userRepository.existsByResidentRegistrationNumberHash(residentRegistrationNumberHash)) {
            throw DuplicatedUserException("이미 가입된 주민등록번호입니다.")
        }
    }

	fun getMyInfo(username: String): UserDto {
		val user = userRepository.findByUsername(username)
			?: throw NotFoundException("사용자를 찾을 수 없습니다. : $username")

		return UserDto(
			id = user.id!!,
			username = user.username,
			name = user.name,
			phone = user.phone,
			topLevelAddress = user.topLevelAddress,
		)
	}
}
