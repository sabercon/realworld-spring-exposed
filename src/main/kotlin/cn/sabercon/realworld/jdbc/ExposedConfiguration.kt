package cn.sabercon.realworld.jdbc

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.context.annotation.Configuration

/**
 * Exposed 0.41.1 is not compatible with Spring Boot 3, so we need to import [ExposedAutoConfiguration] manually.
 */
@ImportAutoConfiguration(
    value = [ExposedAutoConfiguration::class],
    exclude = [DataSourceTransactionManagerAutoConfiguration::class],
)
@Configuration(proxyBeanMethods = false)
class ExposedConfiguration
