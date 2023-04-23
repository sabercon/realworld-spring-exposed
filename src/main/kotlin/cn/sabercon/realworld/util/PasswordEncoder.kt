package cn.sabercon.realworld.util

import java.security.MessageDigest

object PasswordEncoder {

    /**
     * Returns the encoded password.
     */
    fun encode(rawPassword: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(rawPassword.toByteArray())
            .joinToString(separator = "") { "%02x".format(it) }
    }

    /**
     * Returns `true` if [rawPassword] matches [encodedPassword].
     */
    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encode(rawPassword) == encodedPassword
    }
}
