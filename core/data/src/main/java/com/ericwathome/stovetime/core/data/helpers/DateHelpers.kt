package com.ericwathome.stovetime.core.data.helpers

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

val Now: Instant
    get() = Clock.System.now()

val DeviceTimeZone: TimeZone
    get() = TimeZone.currentSystemDefault()

val Today: LocalDate
    get() = Now.toLocalDateTime(DeviceTimeZone).date

val Long.asLocalDate: LocalDate
    get() = Instant.fromEpochMilliseconds(this).toLocalDateTime(DeviceTimeZone).date
