package dev.matzat.numberconverter.persistence.config

import dev.matzat.numberconverter.persistence.BaseEntity
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = [BaseEntity::class])
@EntityScan(basePackageClasses = [BaseEntity::class])
class JpaConfiguration
