package dev.matzat.numberconverter.controller

import dev.matzat.numberconverter.model.AuditLogPage
import dev.matzat.numberconverter.model.toAuditLogPage
import dev.matzat.numberconverter.persistence.AuditLogRepository
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/audit-log")
class AuditLogController(
    private val auditLogRepository: AuditLogRepository,
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponse(
        responseCode = "200",
        description = "A page of audit logs",
    )
    @ResponseStatus(HttpStatus.OK)
    suspend fun listAuditLogs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "created") sort: String,
    ): ResponseEntity<AuditLogPage> {
        val pageRequest = PageRequest.of(page, size, Sort.by(sort))
        return ResponseEntity
            .ok()
            .body(
                auditLogRepository
                    .findAll(pageRequest)
                    .toAuditLogPage(pageRequest),
            )
    }
}
