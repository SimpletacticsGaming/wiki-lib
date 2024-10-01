package de.simpletactics.wiki.lib.adapter.persistence.mapper

import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollModel


fun PollEntity.toModel() = PollModel(
    id,
    question,
    pollEntries,
    description,
    ended,
    if (date != null) Date.getZoneDateTimeFrom(date) else null,
)


fun PollModel.toEntity() = PollEntity(
    id,
    question,
    pollEntries,
    description,
    ended,
    if (date != null) Date.getSqlDateFrom(date) else null,
)