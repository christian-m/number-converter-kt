package dev.matzat.numberconverter.persistence

import jakarta.persistence.Entity
import java.time.Instant

@Entity(name = "audit_log")
class AuditLog(
    var timestamp: Instant,
    var input: String,
    var output: String,
    var statusCode: String,
    var success: Boolean,
) : BaseEntity()
