package cn.sabercon.realworld.user

import cn.sabercon.realworld.ContainerConfiguration
import cn.sabercon.realworld.RealworldApplication
import cn.sabercon.realworld.util.tx
import com.jayway.jsonpath.JsonPath
import io.kotest.core.spec.style.FunSpec
import org.jetbrains.exposed.sql.deleteAll
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@SpringBootTest(classes = [RealworldApplication::class, ContainerConfiguration::class])
class UserWebIntegrationTest(client: MockMvc) : FunSpec({

    afterEach { tx { Users.deleteAll() } }

    val testUsername = "saber"
    val testEmail = "hello@qq.com"
    val testPwd = "123456"

    fun register(username: String = testUsername, email: String = testEmail, pwd: String = testPwd): ResultActionsDsl {
        return client.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "user": { "username": "$username", "email": "$email", "password": "$pwd" } } """
        }
    }

    fun login(email: String = testEmail, pwd: String = testPwd): ResultActionsDsl {
        return client.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "user": { "email": "$email", "password": "$pwd" } } """
        }
    }

    fun getUser(token: String): ResultActionsDsl {
        return client.get("/user") {
            header("Authorization", "Token $token")
        }
    }

    context("Registration") {

        test("Returns the created user with token when registration succeeds") {
            register().andExpect {
                status { isOk() }
                jsonPath("$.user.email") { value(testEmail) }
                jsonPath("$.user.username") { value(testUsername) }
                jsonPath("$.user.token") { isString() }
            }
        }

        test("Returns errors when the email exists") {
            register().andExpect { status { isOk() } }

            register(username = "_$testUsername", pwd = "_$testPwd").andExpect { status { isUnprocessableEntity() } }
        }

        test("Returns errors when the username exists") {
            register().andExpect { status { isOk() } }

            register(email = "_$testEmail", pwd = "_$testPwd").andExpect { status { isUnprocessableEntity() } }
        }
    }

    context("Authentication") {

        test("Returns the user with token when login succeeds") {
            register().andExpect { status { isOk() } }

            login().andExpect {
                status { isOk() }
                jsonPath("$.user.email") { value(testEmail) }
                jsonPath("$.user.username") { value(testUsername) }
                jsonPath("$.user.token") { isString() }
            }
        }

        test("Returns errors when the email does not exist") {
            login().andExpect { status { isUnprocessableEntity() } }
        }

        test("Returns errors when the password is wrong") {
            register().andExpect { status { isOk() } }

            login(pwd = "invalid_password").andExpect { status { isUnprocessableEntity() } }
        }
    }

    context("Get Current User") {

        test("Returns the current user when the token is from registration") {
            val token = register().andReturn().response.contentAsString
                .let { JsonPath.read<String>(it, "$.user.token") }

            getUser(token).andExpect {
                status { isOk() }
                jsonPath("$.user.email") { value(testEmail) }
                jsonPath("$.user.username") { value(testUsername) }
                jsonPath("$.user.token") { isString() }
            }
        }

        test("Returns the current user when the token is from authentication") {
            register().andExpect { status { isOk() } }
            val token = login().andReturn().response.contentAsString
                .let { JsonPath.read<String>(it, "$.user.token") }

            getUser(token).andExpect {
                status { isOk() }
                jsonPath("$.user.email") { value(testEmail) }
                jsonPath("$.user.username") { value(testUsername) }
                jsonPath("$.user.token") { isString() }
            }
        }

        test("Returns errors when the token is invalid") {
            getUser("invalid_token").andExpect { status { isUnauthorized() } }
        }
    }
})
