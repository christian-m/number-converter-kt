# Number Converter API

## Introduction

This is a web API to convert a value into another format, for example, an arabic number to the appropriate roman number.

### Swagger UI

You can find a Swagger UI endpoint at `http://{{host}}/swagger-ui`

### Convert value

Submit a conversion method (one of `DECIMAL_TO_ROMAN` or `BINARY_TO_ROMAN`) and a value that should be converted:

```
POST http://{{host}}/convert
Content-Type: application/json

{
    "conversionMethod": "DECIMAL_TO_ROMAN",
    "value": "15"
}
```

The result of the service is a plain text:

```
XIV
```

An audit log entry is written to the database for any request to the `/convert` endpoint.

You can get a paged log of the audits using the request:

```
GET http://{{host}}/audit-log
```

### Extension with another number converter

Currently are implemented:

* A converter of a decimal value to a roman number: `DECIMAL_TO_ROMAN`
* A converter of a binary value to a roman number: `BINARY_TO_ROMAN`

To add a new converter:

* Add a new conversion method to the enum `dev.matzat.numberconverter.converter.ConversionMethod`
* Create a class that implements the interface `dev.matzat.numberconverter.converter.Converter`
* Implement the method `boolean supports(final ConversionMethod conversionMethod)` so that it returns true if your new conversion method is given as a parameter
* Implement the method `boolean isValid(final String value)` to validate the input values of your new conversion
* Implement the method `String convert(final String value)` with your new conversion
* Annotate the new converter class as Spring `@Component`

## Local development

### Monitoring

There is a prepared `compose.yml` to use with Docker locally.

* A Prometheus instance can be accessed at `http://localhost:8085/`
* A Grafana instance with a JVM metrics dashboard can be accessed at `http://localhost:8086/d/jvm-actuator-service/java-virtual-machine`

## Motivation

* Converting a Java application to Kotlin
* Getting more practice with Maven again, because I haven't used it for a long time
