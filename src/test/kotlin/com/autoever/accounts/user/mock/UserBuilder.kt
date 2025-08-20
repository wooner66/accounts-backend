package com.autoever.accounts.user.mock

import com.autoever.accounts.jpa.user.User
import com.autoever.accounts.service.user.dto.CreateUserRequestDto

object UserBuilder {
    fun `사용자 Entity 생성`(requestDto: CreateUserRequestDto): User {
        return User(
            username = requestDto.username,
            password = "encodedPassword",
            name = requestDto.name,
            residentRegistrationNumberFront = requestDto.residentRegistrationNumber.take(6),
            residentRegistrationNumberBack = "encryptedValue",
            residentRegistrationNumberHash = "hashedValue",
            phone = requestDto.phoneNumber,
            topLevelAddress = requestDto.address,
            addressDetail = requestDto.addressDetail
        )
    }

    fun `사용자 Entity 목록 생성`(): List<User> {
        return listOf(
            User(
                username = "testuser1",
                password = "encodedPassword",
                name = "김현오1",
                residentRegistrationNumberFront = "123456",
                residentRegistrationNumberBack = "encryptedValue",
                residentRegistrationNumberHash = "hashedValue",
                phone = "01012345678",
                topLevelAddress = "서울시",
                addressDetail = "강남구 대치동1",
            ).apply{
                id = 1L
            },
            User(
                username = "testuser2",
                password = "encodedPassword",
                name = "김현오2",
                residentRegistrationNumberFront = "654321",
                residentRegistrationNumberBack = "encryptedValue",
                residentRegistrationNumberHash = "hashedValue",
                phone = "01087654321",
                topLevelAddress = "부산시",
                addressDetail = "해운대구 우동2",
            ).apply {
                id = 2L
            },
        )
    }
}
