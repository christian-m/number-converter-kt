package dev.matzat.numberconverter.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.hibernate.Hibernate
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @Id
    @UuidGenerator
    lateinit var id: UUID

    @Version
    @Column(updatable = false, nullable = false)
    var version: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    var lastModified: Instant? = null

    @CreatedDate
    @Column(nullable = false)
    var created: Instant? = null

    override fun equals(other: Any?) =
        when {
            this === other -> true
            other == null -> false
            /*
             When we fetch an entity lazy, Hibernate creates a proxy.
             Since the proxy and the actual object are representing the same entity, we need to consider that here.
             */
            Hibernate.getClass(this) != Hibernate.getClass(other) && this.javaClass != other.javaClass -> false
            else -> id == (other as BaseEntity).id
        }

    override fun hashCode(): Int = id.hashCode()

    @Override
    // overridden to avoid potential problems with lazy fetching
    override fun toString() = "${this.javaClass.getSimpleName()} (id=$id, version=$version, created=$created, lastModified=$lastModified), "
}
