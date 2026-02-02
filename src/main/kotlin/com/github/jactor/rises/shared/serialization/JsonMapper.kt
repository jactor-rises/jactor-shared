package com.github.jactor.rises.shared.serialization

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.text.SimpleDateFormat

object JsonMapper {
    val objectMapper: ObjectMapper =
        ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(Jdk8Module())
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
            .setDefaultPropertyInclusion(Include.NON_NULL)
}
