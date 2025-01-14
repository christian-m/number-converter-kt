package dev.matzat.numberconverter.controller.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ServerWebInputException

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class WebExceptionHandlerControllerAdvice(
    private val messageSource: MessageSource,
) {
    companion object {
        const val DESCRIPTION_400 = "The general request body is or parameters are invalid"
        const val DESCRIPTION_422 = "Provided object is not valid"
        const val DESCRIPTION_500 = "Unexpected Error"
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ApiResponse(
        responseCode = "422",
        description = DESCRIPTION_422,
        content = [
            Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = Schema(implementation = ProblemDetail::class),
            ),
        ],
    )
    fun handleIllegalArgumentException(exception: IllegalArgumentException) = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.message)

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = DESCRIPTION_400,
        content = [
            Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = Schema(implementation = ProblemDetail::class),
            ),
        ],
    )
    fun handleServerWebInputException(exception: ServerWebInputException) = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ApiResponse(
        responseCode = "422",
        description = DESCRIPTION_422,
        content = [
            Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = Schema(implementation = ProblemDetail::class),
            ),
        ],
    )
    @Suppress("unused")
    fun handleNoSuchElementException(exception: NoSuchElementException) =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            messageSource.getMessage(
                "dev.matzat.numberconverter.controller.config.WebExceptionHandlingControllerAdvice.converterNotFound",
                emptyArray<String>(),
                LocaleContextHolder.getLocale(),
            ),
        )

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ApiResponse(
        responseCode = "422",
        description = DESCRIPTION_422,
        content = [
            Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema =
                    Schema(
                        implementation = ProblemDetail::class,
                    ),
            ),
        ],
    )
    fun handleConstraintViolationException(exception: ConstraintViolationException): ProblemDetail {
        val fieldErrors = exception.constraintViolations.map { FieldError(it.rootBean.toString(), it.propertyPath.toString(), it.message) }
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, fieldErrors.toConcatenatedString())
        problemDetail.properties = mapOf("fieldErrorDetails" to fieldErrors.toMap())
        return problemDetail
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = DESCRIPTION_500,
        content = [
            Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema =
                    Schema(
                        implementation = ProblemDetail::class,
                    ),
            ),
        ],
    )
    fun handleThrowable(exception: Throwable): ProblemDetail {
        val exceptionName = exception.javaClass.name
        logger.error(exception) { "An unexpected error occurred: $exceptionName" }
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            messageSource.getMessage(
                "dev.matzat.numberconverter.controller.config.WebExceptionHandlingControllerAdvice.unexpectedError",
                arrayOf(exceptionName),
                LocaleContextHolder.getLocale(),
            ),
        )
    }

    fun List<FieldError>.toConcatenatedString() = joinToString { "${it.field}: ${it.defaultMessage}" }

    private fun List<FieldError>.toMap(): MutableMap<String, MutableSet<String?>> {
        val detailFieldErrorMap = mutableMapOf<String, MutableSet<String?>>()
        this
            .map { it.field to it.defaultMessage }
            .map { detailFieldErrorMap.getOrPut(it.first, ::mutableSetOf).add(it.second) }
        return detailFieldErrorMap
    }
}
