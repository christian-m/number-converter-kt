package dev.matzat.numberconverter

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Suppress("UtilityClassWithPublicConstructor")
abstract class SpringBootTestBase {
    companion object {
        @ServiceConnection
        @Suppress("unused")
        val postgresqlContainer = PostgreSQLContainer("postgres:14").also { it.start() }
    }
}
