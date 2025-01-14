package dev.matzat.numberconverter.converter

import assertk.assertThat
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(ConverterResolver::class, DecimalToRomanConverter::class, BinaryToRomanConverter::class)
class ConverterResolverTest(
    @Autowired
    private val converterResolver: ConverterResolver,
) {
    @Test
    @DisplayName(
        "GIVEN a decimal to roman converter is registered " +
            "WHEN a decimal to roman converter is requested " +
            "THEN a decimal to roman converter instance is returned",
    )
    fun resolveDecimalToRomanConverterTest() {
        val converter = converterResolver.resolve(ConversionMethod.DECIMAL_TO_ROMAN)
        assertThat(converter).isInstanceOf(DecimalToRomanConverter::class)
    }

    @Test
    @DisplayName(
        "GIVEN a binary to roman converter is registered " +
            "WHEN a binary to roman converter is requested " +
            "THEN a binary to roman converter instance is returned",
    )
    fun resolveBinaryToRomanConverterTest() {
        val converter = converterResolver.resolve(ConversionMethod.BINARY_TO_ROMAN)
        assertThat(converter).isInstanceOf(BinaryToRomanConverter::class)
    }

    @Test
    @DisplayName(
        "GIVEN a decimal to roman converter is registered " +
            "WHEN a binary to roman converter is requested " +
            "THEN a NoSuchElementException is thrown",
    )
    fun unknownConverterTest() {
        val converterResolver = ConverterResolver(setOf(DecimalToRomanConverter()))
        assertThrows<NoSuchElementException> { converterResolver.resolve(ConversionMethod.BINARY_TO_ROMAN) }
    }
}
