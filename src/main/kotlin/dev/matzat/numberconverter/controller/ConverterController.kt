package dev.matzat.numberconverter.controller

import dev.matzat.numberconverter.converter.ConverterResolver
import dev.matzat.numberconverter.model.ConversionRequest
import dev.matzat.numberconverter.validation.ValidConversionRequest
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/convert")
@Validated
class ConverterController(
    private val converterResolver: ConverterResolver,
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    @ApiResponse(
        responseCode = "200",
        description = "The converted value",
    )
    @ResponseStatus(HttpStatus.OK)
    suspend fun convert(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Value to convert and conversion strategy",
            required = true,
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ConversionRequest::class),
                    examples = [ExampleObject(value = "{ \"conversionMethod\": \"DECIMAL_TO_ROMAN\", \"value\": \"22\" }")],
                ),
            ],
        )
        @Valid
        @ValidConversionRequest
        @RequestBody
        conversionRequest: ConversionRequest,
    ): ResponseEntity<String> {
        val value = converterResolver.resolve(conversionRequest.conversionMethod).convert(conversionRequest.value)
        return ResponseEntity.ok().body(value)
    }
}
