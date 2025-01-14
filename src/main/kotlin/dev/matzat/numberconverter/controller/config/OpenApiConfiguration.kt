package dev.matzat.numberconverter.controller.config

import io.swagger.v3.core.jackson.ModelResolver.enumsAsRef
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springdoc.core.customizers.GlobalOpenApiCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info =
        Info(
            title = "Number Converter API",
            description = "This is a web service to convert a number into another value format.",
        ),
)
class OpenApiConfiguration {
    init {
        enumsAsRef = true
    }

    @Bean
    fun openApiBuildPropertiesVersionCustomizer(
        @Autowired(required = false) buildProperties: BuildProperties?,
    ) = GlobalOpenApiCustomizer { openApi ->
        openApi.info.version = buildProperties?.version ?: "dev-local"
    }
}
