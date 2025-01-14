package dev.matzat.numberconverter.controller.config

import dev.matzat.numberconverter.persistence.AuditLog
import dev.matzat.numberconverter.persistence.AuditLogRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.time.Clock

private val logger = KotlinLogging.logger { }

@Component
class AuditWebFilter(
    private val clock: Clock,
    private val auditLogRepository: AuditLogRepository,
) : WebFilter {
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val timestamp = clock.instant()

        val request = exchange.request
        val requestDecorator = RequestDecorator(request)

        val response = exchange.response
        val responseDecorator = ResponseDecorator(response, request)

        return chain
            .filter(
                object : ServerWebExchangeDecorator(exchange) {
                    override fun getRequest(): ServerHttpRequest = requestDecorator

                    override fun getResponse(): ServerHttpResponse = responseDecorator
                },
            ).doFinally { _ ->
                val uriPath = requestDecorator.uri.getPath()
                if (uriPath.startsWith("/convert")) {
                    val input = requestDecorator.requestBody
                    val output = responseDecorator.responseBody
                    val responseStatusCode = response.statusCode
                    val statusCode = responseStatusCode?.toString() ?: "unknown"
                    val success = responseDecorator.success()
                    logger.debug { "Request Body: $input" }
                    logger.debug { "Response Body: $output" }
                    logger.debug { "Response Status: $statusCode" }
                    auditLogRepository.save(AuditLog(timestamp, input, output, statusCode, success))
                }
            }
    }
}

class ResponseDecorator(
    response: ServerHttpResponse,
    private val request: ServerHttpRequest,
) : ServerHttpResponseDecorator(response) {
    lateinit var responseBody: String

    fun success() = statusCode == HttpStatus.OK

    override fun writeWith(body: Publisher<out DataBuffer>) =
        super.writeWith(
            Flux
                .from(body)
                .doOnNext { responseBuffer: DataBuffer ->
                    val uriPath = request.uri.getPath()
                    if (uriPath.startsWith("/convert")) {
                        runCatching {
                            val bodyStream = ByteArrayOutputStream()
                            val byteBuffer = ByteBuffer.allocate(responseBuffer.readableByteCount())
                            responseBuffer.toByteBuffer(byteBuffer)
                            Channels.newChannel(bodyStream).write(byteBuffer.asReadOnlyBuffer())
                            responseBody = bodyStream.toString(StandardCharsets.UTF_8)
                        }.onFailure {
                            logger.debug(it.cause) { "parsing response body failed" }
                        }
                    }
                },
        )
}

class RequestDecorator(
    request: ServerHttpRequest,
) : ServerHttpRequestDecorator(request) {
    lateinit var requestBody: String

    override fun getBody(): Flux<DataBuffer> =
        super.getBody().doOnNext { requestBuffer: DataBuffer ->
            runCatching {
                val bodyStream = ByteArrayOutputStream()
                val byteBuffer = ByteBuffer.allocate(requestBuffer.readableByteCount())
                requestBuffer.toByteBuffer(byteBuffer)
                Channels.newChannel(bodyStream).write(byteBuffer.asReadOnlyBuffer())
                requestBody = bodyStream.toString(StandardCharsets.UTF_8)
            }.onFailure {
                logger.debug(it.cause) { "parsing request body failed" }
            }
        }
}
