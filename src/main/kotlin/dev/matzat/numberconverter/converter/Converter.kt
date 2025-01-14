package dev.matzat.numberconverter.converter

sealed interface Converter {
    fun isValid(value: String): Boolean

    fun convert(value: String): String

    fun supports(conversionMethod: ConversionMethod): Boolean
}

enum class ConversionMethod {
    DECIMAL_TO_ROMAN,
    BINARY_TO_ROMAN,
    ROMAN_TO_DECIMAL,
    DECIMAL_TO_WORD,
}
