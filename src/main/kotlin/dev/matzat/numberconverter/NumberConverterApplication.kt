package dev.matzat.numberconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@SpringBootApplication
class NumberConverterApplication

fun main(args: Array<String>) {
    runApplication<NumberConverterApplication>(args = args)
}

@Configuration
class BasicConfiguration {
    @Bean
    @ConditionalOnMissingBean(Clock::class)
    fun providingBasicClock(): Clock = Clock.systemUTC()
}
