# jactor-shared
![](https://github.com/jactor-rises/jactor-shared/workflows/deploy%20maven%20artifact/badge.svg)

Shared beans between jactor-persistence and jactor-beans (mostly http api)

## Http api

### Dto
Data transfer objects for the data model of jactor-persistence

### Commands
Command objects used for manipulation the data model of jactor-persistence

## Changelog
version | type of change | description
--------|----------------|------------------------
  0.1.3 | changed        | kotlin and spring-boot version
  0.1.2 | changed        | kotlin and java version
  0.1.1 | changed        | `CreateUserCommandDto`: added swagger documentation
  0.1.0 | created        | `CreateUserCommandDto`: used to create a user on `jactor-persistence`
  0.0.1 | created        | `UserDto`, `PersonDto`, `AddressDto`: Main dto's of the data model
