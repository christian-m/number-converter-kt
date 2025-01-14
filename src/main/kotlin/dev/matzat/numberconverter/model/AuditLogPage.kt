package dev.matzat.numberconverter.model

import dev.matzat.numberconverter.persistence.AuditLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

data class AuditLogPage(
    val content: List<AuditLogDto>,
    val pageable: PageRequest,
    val total: Long,
)

fun Page<AuditLog>.toAuditLogPage(pageRequest: PageRequest): AuditLogPage {
    val auditLogDtos = content.map { auditLog -> auditLog.toAuditLogDto() }
    return AuditLogPage(auditLogDtos, pageRequest, totalElements)
}
