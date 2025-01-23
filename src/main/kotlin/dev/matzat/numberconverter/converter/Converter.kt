package dev.matzat.numberconverter.converter

sealed interface Converter {
    fun supports(conversionMethod: ConversionMethod): Boolean

    fun isValid(value: String): Boolean

    fun convert(value: String): String
}

abstract class BaseConverter : Converter {
    @Throws(IllegalArgumentException::class)
    final override fun convert(value: String): String {
        require(isValid(value)) {
            "Submitted input '$value' is not a valid value or in the supported range"
        }
        return conversion(value)
    }

    abstract fun conversion(value: String): String
}

enum class ConversionMethod {
    DECIMAL_TO_ROMAN,
    BINARY_TO_ROMAN,
}
