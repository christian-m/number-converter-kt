package dev.matzat.numberconverter.converter

import org.springframework.stereotype.Component

@Component
class ConverterResolver(
    private val converters: Set<Converter>,
) {
    @Throws(NoSuchElementException::class)
    fun resolve(conversionMethod: ConversionMethod): Converter =
        converters.firstOrNull { it.supports(conversionMethod) }
            ?: throw NoSuchElementException("Converter for conversion method $conversionMethod not found")
}
