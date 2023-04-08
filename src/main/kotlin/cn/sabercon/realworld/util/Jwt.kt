package cn.sabercon.realworld.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class Jwt(key: String) {

    private val algorithm = Algorithm.HMAC256(key)

    private val verifier = JWT.require(algorithm).build()

    private val expiration = 30.days.toJavaDuration()

    fun createToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withExpiresAt(Instant.now() + expiration)
            .sign(algorithm)
    }

    /**
     * Returns the user id in the token.
     *
     * @throws JWTVerificationException if the token is invalid
     */
    fun decodeToken(token: String): String {
        return verifier.verify(token).subject
    }
}
