package dev.matzat.numberconverter.controller

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import com.ninjasquad.springmockk.MockkBean
import dev.matzat.numberconverter.converter.BinaryToRomanConverter
import dev.matzat.numberconverter.converter.ConversionMethod
import dev.matzat.numberconverter.converter.ConverterResolver
import dev.matzat.numberconverter.converter.DecimalToRomanConverter
import dev.matzat.numberconverter.model.ConversionRequest
import dev.matzat.numberconverter.persistence.AuditLog
import dev.matzat.numberconverter.persistence.AuditLogRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.Clock
import java.time.Instant
import java.util.UUID

@WebFluxTest(ConverterController::class)
class ConverterControllerTest(
    @Autowired
    private val webClient: WebTestClient,
) {
    companion object {
        val MOCK_AUDIT_LOG =
            AuditLog(Instant.now(), "input", "output", "status", false)
                .apply {
                    id = UUID.randomUUID()
                }
    }

    @MockkBean
    private lateinit var converterResolver: ConverterResolver

    @MockkBean
    private lateinit var decimalToRomanConverter: DecimalToRomanConverter

    @MockkBean
    private lateinit var binaryToRomanConverter: BinaryToRomanConverter

    @MockkBean
    private lateinit var auditLogRepository: AuditLogRepository

    @MockkBean
    private lateinit var clock: Clock

    @Test
    @DisplayName("WHEN a valid decimal value is submitted THEN a converted value is returned")
    fun testConversionControllerValidDecimalValue() {
        val givenValue = "15"
        val expectedValue = "XV"
        every { converterResolver.resolve(ConversionMethod.DECIMAL_TO_ROMAN) } returns decimalToRomanConverter
        every { decimalToRomanConverter.isValid(givenValue) } returns true
        every { decimalToRomanConverter.convert(givenValue) } returns expectedValue
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ConversionRequest(ConversionMethod.DECIMAL_TO_ROMAN, givenValue)))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String::class.java)
            .isEqualTo(expectedValue)
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN an invalid decimal value is submitted THEN the HTTP status 422 is returned")
    fun testConversionControllerInvalidDecimalValue() {
        val givenValue = "XA"
        every { converterResolver.resolve(ConversionMethod.DECIMAL_TO_ROMAN) } returns decimalToRomanConverter
        every { decimalToRomanConverter.isValid(givenValue) } returns false
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ConversionRequest(ConversionMethod.DECIMAL_TO_ROMAN, givenValue)))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody(ProblemDetail::class.java)
            .consumeWith { response ->
                assertThat(response.getResponseBody()).isNotNull().isInstanceOf(ProblemDetail::class).all {
                    prop(ProblemDetail::getDetail).isEqualTo("convert.conversionRequest.value: The submitted value is not valid for the submitted conversion method")
                    prop(ProblemDetail::getStatus).isEqualTo(422)
                }
            }
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN a valid binary value is submitted THEN a converted value is returned")
    fun testConversionControllerValidBinaryValue() {
        val givenValue = "1111"
        val expectedValue = "XV"
        every { converterResolver.resolve(ConversionMethod.BINARY_TO_ROMAN) } returns binaryToRomanConverter
        every { binaryToRomanConverter.isValid(givenValue) } returns true
        every { binaryToRomanConverter.convert(givenValue) } returns expectedValue
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ConversionRequest(ConversionMethod.BINARY_TO_ROMAN, givenValue)))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String::class.java)
            .isEqualTo(expectedValue)
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN an invalid binary value is submitted THEN the HTTP status 422 is returned")
    fun testConversionControllerInvalidBinaryValue() {
        val givenValue = "XA"
        every { converterResolver.resolve(ConversionMethod.BINARY_TO_ROMAN) } returns binaryToRomanConverter
        every { binaryToRomanConverter.isValid(givenValue) } returns false
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ConversionRequest(ConversionMethod.BINARY_TO_ROMAN, givenValue)))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody(ProblemDetail::class.java)
            .consumeWith { response ->
                assertThat(response.getResponseBody()).isNotNull().isInstanceOf(ProblemDetail::class).all {
                    prop(ProblemDetail::getDetail).isEqualTo("convert.conversionRequest.value: The submitted value is not valid for the submitted conversion method")
                    prop(ProblemDetail::getStatus).isEqualTo(422)
                }
            }
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN an invalid conversion method is submitted THEN the HTTP status 400 is returned")
    fun testConversionControllerInvalidConversionMethod() {
        val givenValue = "15"
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue("{\"conversionMethod\":\"INVALID\",\"value\":\"" + givenValue + "\"}"))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ProblemDetail::class.java)
            .consumeWith { response ->
                assertThat(response.getResponseBody()).isNotNull().isInstanceOf(ProblemDetail::class).all {
                    prop(ProblemDetail::getDetail).isEqualTo("400 BAD_REQUEST \"Failed to read HTTP message\"")
                    prop(ProblemDetail::getStatus).isEqualTo(400)
                }
            }
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN an conversion method is submitted that have no converter implemented THEN the HTTP status 422 is returned")
    fun testConversionControllerConversionMethodNotFound() {
        val givenValue = "15"
        every { converterResolver.resolve(ConversionMethod.DECIMAL_TO_ROMAN) } throws NoSuchElementException()
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(ConversionRequest(ConversionMethod.DECIMAL_TO_ROMAN, givenValue)),
            ).exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody(ProblemDetail::class.java)
            .consumeWith { response ->
                assertThat(response.getResponseBody()).isNotNull().isInstanceOf(ProblemDetail::class).all {
                    prop(ProblemDetail::getDetail).isEqualTo("convert.conversionRequest.conversionMethod: No converter found for conversion method")
                    prop(ProblemDetail::getStatus).isEqualTo(422)
                }
            }
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }

    @Test
    @DisplayName("WHEN an unexpected exception occurs THEN the HTTP status 500 is returned")
    fun testConversionControllerInternalServerError() {
        val givenValue = "15"
        every { converterResolver.resolve(ConversionMethod.DECIMAL_TO_ROMAN) } throws RuntimeException()
        every { auditLogRepository.save(MOCK_AUDIT_LOG) } returns MOCK_AUDIT_LOG
        every { clock.instant() } returns Clock.systemUTC().instant()
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(ConversionRequest(ConversionMethod.DECIMAL_TO_ROMAN, givenValue)),
            ).exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectBody(ProblemDetail::class.java)
            .consumeWith { response ->
                assertThat(response.getResponseBody()).isNotNull().isInstanceOf(ProblemDetail::class).all {
                    prop(ProblemDetail::getDetail).isEqualTo("An unexpected error occurred")
                    prop(ProblemDetail::getStatus).isEqualTo(500)
                }
            }
        verify(exactly = 1) { auditLogRepository.save(any<AuditLog>()) }
    }
}
