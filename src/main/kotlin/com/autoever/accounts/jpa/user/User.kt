package com.autoever.accounts.jpa.user

import com.autoever.accounts.jpa.common.BaseEntity
import jakarta.persistence.Entity

@Entity(name = "user")
class User(
    val username: String,
    val password: String,
    val name: String,
    val residentRegistrationNumber: String,
    val phoneNumber: String,
    // 시 도 구/군 동/읍 면 리 순으로 큰 단위부터 들어왔다는 전제(검증 생략)
    val address: String,
    val addressDetail: String,
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (username != other.username) return false
        if (password != other.password) return false
        if (name != other.name) return false
        if (residentRegistrationNumber != other.residentRegistrationNumber) return false
        if (phoneNumber != other.phoneNumber) return false
        if (address != other.address) return false
        if (addressDetail != other.addressDetail) return false

        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()
}