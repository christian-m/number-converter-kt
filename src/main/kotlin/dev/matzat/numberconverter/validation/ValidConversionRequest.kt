package dev.matzat.numberconverter.validation

import dev.matzat.numberconverter.converter.ConverterResolver
import dev.matzat.numberconverter.model.ConversionRequest
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraintvalidation.SupportedValidationTarget
import jakarta.validation.constraintvalidation.ValidationTarget
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [ConversionRequestValidator::class])
annotation class ValidConversionRequest(
    val message: String = "{dev.matzat.numberconverter.validation.ConversionRequestValidation.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
class ConversionRequestValidator(
    private val converterResolver: ConverterResolver,
) : ConstraintValidator<ValidConversionRequest, ConversionRequest> {
    companion object {
        const val CONVERTER_NOT_FOUND_MESSAGE_TEMPLATE = "{dev.matzat.numberconverter.validation.ConversionRequestValidation.converterNotFound}"
    }

    private lateinit var annotation: ValidConversionRequest

    override fun initialize(constraintAnnotation: ValidConversionRequest) {
        super.initialize(constraintAnnotation)
        annotation = constraintAnnotation
    }

    override fun isValid(
        conversionRequest: ConversionRequest,
        context: ConstraintValidatorContext,
    ) = try {
        val converter = converterResolver.resolve(conversionRequest.conversionMethod)
        if (converter.isValid(conversionRequest.value)) {
            true
        } else {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(annotation.message)
                .addPropertyNode("value")
                .addConstraintViolation()
            false
        }
    } catch (e: NoSuchElementException) {
        context.disableDefaultConstraintViolation()
        context
            .buildConstraintViolationWithTemplate(CONVERTER_NOT_FOUND_MESSAGE_TEMPLATE)
            .addPropertyNode("conversionMethod")
            .addConstraintViolation()
        false
    }
}
