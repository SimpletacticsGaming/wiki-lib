package de.simpletactics.wiki.lib.adapter.dto.poll

import java.sql.Date
import java.time.ZonedDateTime

data class PollEntity(
    val id: Int?,
    val question: String,
    val description: String = "",
    val pollEntries: List<PollEntryEntity>,
    val ended: Boolean = false,
    val date: Date? = null,
)

data class PollModel(
    val id: Int?,
    val question: String,
    val description: String = "",
    val pollEntries: List<PollEntryEntity>,
    val ended: Boolean = false,
    val date: ZonedDateTime? = null,
)