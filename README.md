# jactor-shared

[![build and test](https://github.com/jactor-rises/jactor-shared/actions/workflows/build-test.yaml/badge.svg?label=build%20and%20test&cachebust=20260208)](https://github.com/jactor-rises/jactor-shared/actions/workflows/build-test.yaml)

Shared beans between [jactor-persistence](https://github.com/jactor-rises/jactor-persistence) and
[jactor-web](https://github.com/jactor-rises/jactor-web) http api and json-serialization

will be available on [Github packages](https://github.com/jactor-rises/jactor-shared/releases)

## Http api

### Dto

Data transfer objects for the data model of jactor-persistence

### Commands

Command objects used for manipulation the data model of jactor-persistence

## Serialization

ObjectMapper for mapping json to/from dto and command objects

## Misc

- `BooleanExtension.kt`            : Kotlin util with callback according to boolean value
- `SpringBeanNames.kt`             : Listing of bean names in a spring-context
- `StackTraceElementExtensions.kt` : Kotlin util on StackTraceElement operations
- `ThrowableExtensions.kt`         : Kotlin util on Throwable operations
