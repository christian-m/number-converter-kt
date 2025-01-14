package dev.matzat.numberconverter.model

import dev.matzat.numberconverter.persistence.AuditLog
import java.time.Instant

data class AuditLogDto(
    val timestamp: Instant,
    val input: String,
    val output: String,
    val statusCode: String,
    val success: Boolean,
)

fun AuditLog.toAuditLogDto() = AuditLogDto(timestamp, input, output, statusCode, success)
