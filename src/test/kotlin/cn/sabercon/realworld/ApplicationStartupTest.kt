package cn.sabercon.realworld

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest(classes = [RealworldApplication::class, ContainerConfiguration::class])
class ApplicationStartupTest(client: MockMvc) : FunSpec({

    test("Health check") {
        client.get("/actuator/health")
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("status") { value("UP") } }
    }
})
