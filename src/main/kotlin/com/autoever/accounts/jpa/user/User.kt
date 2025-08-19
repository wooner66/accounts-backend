package com.autoever.accounts.jpa.user

import com.autoever.accounts.jpa.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    val username: String,
    val password: String,
    val name: String,
    @Column(name = "rrn_front")
    val residentRegistrationNumberFront: String,
    @Column(name = "rrn_back")
    val residentRegistrationNumberBack: String,
    @Column(name = "rrn_hash")
    val residentRegistrationNumberHash: String,
    val phone: String,
    // 시 도 구/군 동/읍 면 리 순으로 큰 단위부터 들어왔다는 전제(검증 생략)
    val topLevelAddress: String,
    val addressDetail: String,
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (username != other.username) return false
        if (password != other.password) return false
        if (name != other.name) return false
        if (residentRegistrationNumberFront != other.residentRegistrationNumberFront) return false
        if (residentRegistrationNumberBack != other.residentRegistrationNumberBack) return false
        if (residentRegistrationNumberHash != other.residentRegistrationNumberHash) return false
        if (phone != other.phone) return false
        if (topLevelAddress != other.topLevelAddress) return false
        if (addressDetail != other.addressDetail) return false

        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
