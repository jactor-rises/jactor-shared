package com.github.jactor.rises.shared.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import java.text.SimpleDateFormat

object JsonMapper {
    val objectMapper: ObjectMapper = JsonMapper.builder()
        .defaultDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        .changeDefaultPropertyInclusion { current ->
            current.withValueInclusion(JsonInclude.Include.NON_NULL)
        }
        .build()
}
