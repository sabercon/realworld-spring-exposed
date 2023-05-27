package cn.sabercon.realworld

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

class ContainerConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer() = PostgreSQLContainer("postgres")
}
