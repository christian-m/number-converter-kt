package dev.matzat.numberconverter.controller

import com.ninjasquad.springmockk.MockkBean
import dev.matzat.numberconverter.persistence.AuditLog
import dev.matzat.numberconverter.persistence.AuditLogRepository
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Clock
import java.time.Instant

@WebFluxTest(AuditLogController::class)
class AudtiLogControllerTest(
    @Autowired
    private val webClient: WebTestClient,
) {
    companion object {
        private val MOCK_AUDIT_LOCK_PAGE =
            PageImpl(
                listOf(
                    AuditLog(Instant.ofEpochMilli(1734104483000L), "input 1", "output 1", "200 OK", true),
                    AuditLog(Instant.ofEpochMilli(1734104543000L), "input 2", "output 2", "200 OK", true),
                    AuditLog(Instant.ofEpochMilli(1734104603000L), "input 3", "output 3", "422 UNPROCESSABLE_ENTITY", false),
                    AuditLog(Instant.ofEpochMilli(1734104663000L), "input 4", "output 4", "422 UNPROCESSABLE_ENTITY", false),
                ),
                Pageable.unpaged(),
                4,
            )
    }

    @MockkBean
    private lateinit var auditLogRepository: AuditLogRepository

    @MockkBean
    private lateinit var clock: Clock

    @Test
    @DisplayName("GIVEN four audit log entries in the database WHEN a audit log page is requested THEN the list with all entries is returned")
    fun testAuditLogPageAll() {
        every { auditLogRepository.findAll(any<Pageable>()) } returns MOCK_AUDIT_LOCK_PAGE
        every { clock.instant() } returns Instant.ofEpochMilli(1734104723000L)
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
}
