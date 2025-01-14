package dev.matzat.numberconverter.converter

import org.springframework.stereotype.Component

@Component
class BinaryToRomanConverter(
    private val decimalToRomanConverter: DecimalToRomanConverter,
) : Converter {
    companion object {
        private const val MAX_SUPPORTED_INPUT_VALUE = 3999
    }

    override fun supports(conversionMethod: ConversionMethod) = conversionMethod == ConversionMethod.BINARY_TO_ROMAN

    override fun isValid(value: String) =
        when {
            !value.matches(Regex("[01]+")) -> false
            else ->
                if (runCatching { Integer.parseInt(value, 2) }.isSuccess) {
                    Integer.parseInt(value, 2) <= MAX_SUPPORTED_INPUT_VALUE
                } else {
                    false
                }
        }

    @Throws(IllegalArgumentException::class)
    override fun convert(value: String): String {
        require(isValid(value)) {
            "Submitted input '$value' is not a valid binary value or exceeds the limit of the maximum (${Integer.toBinaryString(MAX_SUPPORTED_INPUT_VALUE)})"
        }
        val input = Integer.parseInt(value, 2)
        return decimalToRomanConverter.convert(input.toString())
    }
}
