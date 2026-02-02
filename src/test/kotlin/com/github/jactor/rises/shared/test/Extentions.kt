package com.github.jactor.rises.shared.test

import java.time.LocalDateTime

fun LocalDateTime.roundDownToMinute(): LocalDateTime = this.withSecond(0).withNano(0)
