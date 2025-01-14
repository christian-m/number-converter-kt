package dev.matzat.numberconverter.controller

import dev.matzat.numberconverter.SpringBootTestBase
import org.flywaydb.test.FlywayTestExecutionListener
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@AutoConfigureWebTestClient
@TestExecutionListeners(
    listeners = [FlywayTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
)
class AuditLogControllerIntegrationTest(
    @Autowired
    private val webClient: WebTestClient,
) : SpringBootTestBase() {
    @Test
    @FlywayTest
    @DisplayName("GIVEN four audit log entries in the database WHEN a audit log page is requested THEN the list with all entries is returned")
    fun testAuditLogPageAll() {
        webClient
            .get()
            .uri("/audit-log")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.content[0].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104483000L))
            .jsonPath("$.content[0].input")
            .isEqualTo("input 1")
            .jsonPath("$.content[0].output")
            .isEqualTo("output 1")
            .jsonPath("$.content[0].statusCode")
            .isEqualTo("200 OK")
            .jsonPath("$.content[0].success")
            .isEqualTo(true)
            .jsonPath("$.content[1].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104543000L))
            .jsonPath("$.content[1].input")
            .isEqualTo("input 2")
            .jsonPath("$.content[1].output")
            .isEqualTo("output 2")
            .jsonPath("$.content[1].statusCode")
            .isEqualTo("200 OK")
            .jsonPath("$.content[1].success")
            .isEqualTo(true)
            .jsonPath("$.content[2].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104603000L))
            .jsonPath("$.content[2].input")
            .isEqualTo("input 3")
            .jsonPath("$.content[2].output")
            .isEqualTo("output 3")
            .jsonPath("$.content[2].statusCode")
            .isEqualTo("422 UNPROCESSABLE_ENTITY")
            .jsonPath("$.content[2].success")
            .isEqualTo(false)
            .jsonPath("$.content[3].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104663000L))
            .jsonPath("$.content[3].input")
            .isEqualTo("input 4")
            .jsonPath("$.content[3].output")
            .isEqualTo("output 4")
            .jsonPath("$.content[3].statusCode")
            .isEqualTo("422 UNPROCESSABLE_ENTITY")
            .jsonPath("$.content[3].success")
            .isEqualTo(false)
            .jsonPath("$.total")
            .isEqualTo(4)
            .jsonPath("$.pageable.pageNumber")
            .isEqualTo(0)
            .jsonPath("$.pageable.pageSize")
            .isEqualTo(10)
            .jsonPath("$.pageable.offset")
            .isEqualTo(0)
    }

    @Test
    @FlywayTest
    @DisplayName("GIVEN four audit log entries in the database WHEN a audit log page 1 is requested THEN the list with two entries is returned")
    fun testAuditLogPageOne() {
        webClient
            .get()
            .uri("/audit-log?page=0&size=2&sort=created")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.content[0].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104483000L))
            .jsonPath("$.content[0].input")
            .isEqualTo("input 1")
            .jsonPath("$.content[0].output")
            .isEqualTo("output 1")
            .jsonPath("$.content[0].statusCode")
            .isEqualTo("200 OK")
            .jsonPath("$.content[0].success")
            .isEqualTo(true)
            .jsonPath("$.content[1].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104543000L))
            .jsonPath("$.content[1].input")
            .isEqualTo("input 2")
            .jsonPath("$.content[1].output")
            .isEqualTo("output 2")
            .jsonPath("$.content[1].statusCode")
            .isEqualTo("200 OK")
            .jsonPath("$.content[1].success")
            .isEqualTo(true)
            .jsonPath("$.total")
            .isEqualTo(4)
            .jsonPath("$.pageable.pageNumber")
            .isEqualTo(0)
            .jsonPath("$.pageable.pageSize")
            .isEqualTo(2)
            .jsonPath("$.pageable.offset")
            .isEqualTo(0)
    }

    @Test
    @FlywayTest
    @DisplayName("GIVEN four audit log entries in the database WHEN a audit log page 1 is requested THEN the list with two entries is returned")
    fun testAuditLogPageTwo() {
        webClient
            .get()
            .uri("/audit-log?page=1&size=2&sort=created")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.content[0].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104603000L))
            .jsonPath("$.content[0].input")
            .isEqualTo("input 3")
            .jsonPath("$.content[0].output")
            .isEqualTo("output 3")
            .jsonPath("$.content[0].statusCode")
            .isEqualTo("422 UNPROCESSABLE_ENTITY")
            .jsonPath("$.content[0].success")
            .isEqualTo(false)
            .jsonPath("$.content[1].timestamp")
            .isEqualTo(Instant.ofEpochMilli(1734104663000L))
            .jsonPath("$.content[1].input")
            .isEqualTo("input 4")
            .jsonPath("$.content[1].output")
            .isEqualTo("output 4")
            .jsonPath("$.content[1].statusCode")
            .isEqualTo("422 UNPROCESSABLE_ENTITY")
            .jsonPath("$.content[1].success")
            .isEqualTo(false)
            .jsonPath("$.total")
            .isEqualTo(4)
            .jsonPath("$.pageable.pageNumber")
            .isEqualTo(1)
            .jsonPath("$.pageable.pageSize")
            .isEqualTo(2)
            .jsonPath("$.pageable.offset")
            .isEqualTo(2)
    }
}
