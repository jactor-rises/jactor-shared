package com.github.jactor.rises.shared.test

import assertk.Assert
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import java.time.LocalDateTime

fun <T> Assert<T?>.assertAll(function: T.() -> Unit) = isNotNull().given { assertk.assertAll { it.function() } }
fun LocalDateTime.roundDownToMinute(): LocalDateTime = this.withSecond(0).withNano(0)
infix fun <T> Assert<T?>.equals(actual: T?): Unit = actual?.let { this.isEqualTo(it) } ?: this.isNull()
infix fun <T> T?.named(name: String): Assert<T?> = assertThat(actual = this, name = name)
infix fun <T : Collection<*>> Assert<T?>.sized(size: Int): Unit = this.isNotNull().hasSize(size = size)
