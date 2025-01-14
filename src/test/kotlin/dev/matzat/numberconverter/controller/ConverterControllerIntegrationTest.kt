package dev.matzat.numberconverter.controller

import assertk.all
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import dev.matzat.numberconverter.SpringBootTestBase
import dev.matzat.numberconverter.converter.ConversionMethod
import dev.matzat.numberconverter.model.ConversionRequest
import dev.matzat.numberconverter.persistence.AuditLogRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@AutoConfigureWebTestClient
class ConverterControllerIntegrationTest(
    @Autowired
    private val webClient: WebTestClient,
    @Autowired
    private val auditLogRepository: AuditLogRepository,
) : SpringBootTestBase() {
    @BeforeEach
    fun setUp() {
        auditLogRepository.deleteAll()
    }

    @Test
    @DisplayName("WHEN a valid decimal value is submitted THEN a converted value is returned")
    fun testConversionControllerValidDecimalValue() {
        val givenValue = "15"
        val expectedValue = "XV"
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
        val auditLogs = auditLogRepository.findAll()
        assertThat(auditLogs as Collection<*>).hasSize(1)
        val auditLog = auditLogs.iterator().next()
        assertThat(auditLog.input).isEqualTo("{\"conversionMethod\":\"DECIMAL_TO_ROMAN\",\"value\":\"15\"}")
        assertThat(auditLog.output).isEqualTo("XV")
        assertThat(auditLog.statusCode).isEqualTo("200 OK")
        assertThat(auditLog.success).isNotNull().isTrue()
    }

    @Test
    @DisplayName("WHEN an invalid decimal value is submitted THEN the HTTP status 422 is returned")
    fun testConversionControllerInvalidDecimalValue() {
        val givenValue = "XA"
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue("{\"conversionMethod\":\"DECIMAL_TO_ROMAN\",\"value\":\"" + givenValue + "\"}"))
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
        val auditLogs = auditLogRepository.findAll()
        assertThat(auditLogs as Collection<*>).hasSize(1)
        val auditLog = auditLogs.iterator().next()
        assertThat(auditLog.input).isEqualTo("{\"conversionMethod\":\"DECIMAL_TO_ROMAN\",\"value\":\"XA\"}")
        assertThat(
            auditLog.output,
        ).isEqualTo(
            "{\"type\":\"about:blank\",\"title\":\"Unprocessable Entity\",\"status\":422,\"detail\":\"convert.conversionRequest.value: The submitted value is not valid for the submitted conversion method\",\"instance\":\"/convert\",\"fieldErrorDetails\":{\"convert.conversionRequest.value\":[\"The submitted value is not valid for the submitted conversion method\"]}}",
        )
        assertThat(auditLog.statusCode).isEqualTo("422 UNPROCESSABLE_ENTITY")
        assertThat(auditLog.success).isNotNull().isFalse()
    }

    @Test
    @DisplayName("WHEN a valid binary value is submitted THEN a converted value is returned")
    fun testConversionControllerValidBinaryValue() {
        val givenValue = "1111"
        val expectedValue = "XV"
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
        val auditLogs = auditLogRepository.findAll()
        assertThat(auditLogs as Collection<*>).hasSize(1)
        val auditLog = auditLogs.iterator().next()
        assertThat(auditLog.input).isEqualTo("{\"conversionMethod\":\"BINARY_TO_ROMAN\",\"value\":\"1111\"}")
        assertThat(auditLog.output).isEqualTo("XV")
        assertThat(auditLog.statusCode).isEqualTo("200 OK")
        assertThat(auditLog.success).isNotNull().isTrue()
    }

    @Test
    @DisplayName("WHEN an invalid binary value is submitted THEN the HTTP status 422 is returned")
    fun testConversionControllerInvalidBinaryValue() {
        val givenValue = "XA"
        webClient
            .post()
            .uri("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue("{\"conversionMethod\":\"BINARY_TO_ROMAN\",\"value\":\"" + givenValue + "\"}"))
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
        val auditLogs = auditLogRepository.findAll()
        assertThat(auditLogs as Collection<*>).hasSize(1)
        val auditLog = auditLogs.iterator().next()
        assertThat(auditLog.input).isEqualTo("{\"conversionMethod\":\"BINARY_TO_ROMAN\",\"value\":\"XA\"}")
        assertThat(
            auditLog.output,
        ).isEqualTo(
            "{\"type\":\"about:blank\",\"title\":\"Unprocessable Entity\",\"status\":422,\"detail\":\"convert.conversionRequest.value: The submitted value is not valid for the submitted conversion method\",\"instance\":\"/convert\",\"fieldErrorDetails\":{\"convert.conversionRequest.value\":[\"The submitted value is not valid for the submitted conversion method\"]}}",
        )
        assertThat(auditLog.statusCode).isEqualTo("422 UNPROCESSABLE_ENTITY")
        assertThat(auditLog.success).isNotNull().isFalse()
    }

    @Test
    @DisplayName("WHEN an invalid conversion method is submitted THEN the HTTP status 400 is returned")
    fun testConversionControllerInvalidConversionMethod() {
        val givenValue = "15"
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
        val auditLogs = auditLogRepository.findAll()
        assertThat(auditLogs as Collection<*>).hasSize(1)
        val auditLog = auditLogs.iterator().next()
        assertThat(auditLog.input).isEqualTo("{\"conversionMethod\":\"INVALID\",\"value\":\"15\"}")
        assertThat(
            auditLog.output,
        ).isEqualTo(
            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"400 BAD_REQUEST \\\"Failed to read HTTP message\\\"\",\"instance\":\"/convert\"}",
        )
        assertThat(auditLog.statusCode).isEqualTo("400 BAD_REQUEST")
        assertThat(auditLog.success).isNotNull().isFalse()
    }
}
