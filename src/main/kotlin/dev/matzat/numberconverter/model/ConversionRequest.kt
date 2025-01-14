package dev.matzat.numberconverter.model

import dev.matzat.numberconverter.converter.ConversionMethod
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ConversionRequest(
    @NotNull
    val conversionMethod: ConversionMethod,
    @NotEmpty
    val value: String,
)
