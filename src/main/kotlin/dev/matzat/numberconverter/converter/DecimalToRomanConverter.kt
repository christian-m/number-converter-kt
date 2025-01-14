package dev.matzat.numberconverter.converter

import org.springframework.stereotype.Component

@Component
class DecimalToRomanConverter : Converter {
    companion object {
        private const val MAX_SUPPORTED_INPUT_VALUE = 3999
        private const val MIN_SUPPORTED_INPUT_VALUE = 0
    }

    override fun supports(conversionMethod: ConversionMethod) = conversionMethod == ConversionMethod.DECIMAL_TO_ROMAN

    override fun isValid(value: String) =
        when {
            !value.matches(Regex("[0-9]+")) -> false
            else ->
                if (runCatching { Integer.parseInt(value) }.isSuccess) {
                    Integer.parseInt(value) in MIN_SUPPORTED_INPUT_VALUE..MAX_SUPPORTED_INPUT_VALUE
                } else {
                    false
                }
        }

    @Throws(IllegalArgumentException::class)
    override fun convert(value: String): String {
        require(isValid(value)) {
            "Submitted input '$value' is not a valid decimal value or is not in the range of $MIN_SUPPORTED_INPUT_VALUE - $MAX_SUPPORTED_INPUT_VALUE"
        }
        var input = value.toInt()
        val roman = StringBuilder()
        RomanBase.entries.forEach { conversionBase ->
            while (input >= conversionBase.base) {
                roman.append(conversionBase.name)
                input -= conversionBase.base
            }
        }
        return roman.toString()
    }

    private enum class RomanBase(
        val base: Int,
    ) {
        M(1000),
        CM(900),
        D(500),
        CD(400),
        C(100),
        XC(90),
        L(50),
        XL(40),
        X(10),
        IX(9),
        V(5),
        IV(4),
        I(1),
    }
}
