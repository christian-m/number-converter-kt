package dev.matzat.numberconverter.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface AuditLogRepository :
    PagingAndSortingRepository<AuditLog, UUID>,
    CrudRepository<AuditLog, UUID> {
    override fun findAll(pageable: Pageable): Page<AuditLog>
}
