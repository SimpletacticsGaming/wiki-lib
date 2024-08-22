package de.simpletactics.wiki.lib.adapter.dto.poll

import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Date {
    fun getDateAsString(): String {
        return ZonedDateTime.now(ZoneId.of("Europe/Berlin")).format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
    }

    fun getDateAsString(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun getDate(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
    }

    fun getStringAsDate(dateAsString: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateAsString, formatter).atStartOfDay(ZoneId.of("Europe/Berlin"))
    }

    fun getStringWithTimeAsDate(dateWithDate: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
        val localDateTime = LocalDateTime.parse(dateWithDate, formatter)
        return localDateTime.atZone(ZoneId.of("Europe/Berlin"))
    }

    fun getZoneDateTimeFrom(sqlDate: Date): ZonedDateTime =
        sqlDate.toLocalDate().atStartOfDay(ZoneId.of("Europe/Berlin"))

    fun getSqlDateFrom(time: ZonedDateTime): Date = Date.valueOf(time.toLocalDate())
}
