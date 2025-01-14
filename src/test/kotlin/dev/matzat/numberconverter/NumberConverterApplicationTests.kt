package dev.matzat.numberconverter

import assertk.assertThat
import assertk.assertions.isNotNull
import dev.matzat.numberconverter.controller.AuditLogController
import dev.matzat.numberconverter.controller.ConverterController
import dev.matzat.numberconverter.converter.BinaryToRomanConverter
import dev.matzat.numberconverter.converter.ConverterResolver
import dev.matzat.numberconverter.converter.DecimalToRomanConverter
import dev.matzat.numberconverter.persistence.AuditLogRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class NumberConverterApplicationTests(
    @Autowired
    private val decimalToRomanConverter: DecimalToRomanConverter,
    @Autowired
    private val binaryToRomanConverter: BinaryToRomanConverter,
    @Autowired
    private val converterResolver: ConverterResolver,
    @Autowired
    private val converterController: ConverterController,
    @Autowired
    private val auditLogRepository: AuditLogController,
    @Autowired
    private val auditLogController: AuditLogRepository,
) : SpringBootTestBase() {
    @Test
    fun contextLoads() {
        assertThat(decimalToRomanConverter).isNotNull()
        assertThat(binaryToRomanConverter).isNotNull()
        assertThat(converterResolver).isNotNull()
        assertThat(converterController).isNotNull()
        assertThat(auditLogController).isNotNull()
        assertThat(auditLogRepository).isNotNull()
    }
}
