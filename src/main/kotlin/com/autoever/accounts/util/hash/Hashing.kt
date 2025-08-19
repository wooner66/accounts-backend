package com.autoever.accounts.util.hash

import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class Hashing {
    fun sha256Hex(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val dig = md.digest(s.toByteArray(Charsets.UTF_8))
        return dig.joinToString("") { "%02x".format(it) }
    }
}
