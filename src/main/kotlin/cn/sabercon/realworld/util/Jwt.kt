package cn.sabercon.realworld.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

/**
 * A utility class for generating and decoding JWT.
 *
 * @param secret the secret used to sign tokens
 * @param expiration the default expiration time of signed tokens
 */
class Jwt(secret: String, private val expiration: Duration = 30.days) {

    private val algorithm = Algorithm.HMAC256(secret)

    private val verifier = JWT.require(algorithm).build()

    /**
     * Returns a token with [userId] as subject.
     * The token will expire in [expiration].
     */
    fun generateToken(userId: String, expiration: Duration = this.expiration): String {
        return JWT.create()
            .withSubject(userId)
            .withExpiresAt(Instant.now() + expiration.toJavaDuration())
            .sign(algorithm)
    }

    /**
     * Returns the user id in [token].
     *
     * @throws JWTVerificationException if [token] is invalid
     */
    fun decodeToken(token: String): String {
        return verifier.verify(token).subject
    }
}
