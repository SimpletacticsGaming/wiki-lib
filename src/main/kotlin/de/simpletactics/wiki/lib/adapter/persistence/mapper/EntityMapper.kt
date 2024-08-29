package de.simpletactics.wiki.lib.adapter.persistence.mapper

import de.simpletactics.model.poll.PollVoteEnum
import de.simpletactics.model.poll.Vote
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

// FIXME: can this be deleted?
private fun getOptionSelectionFromVotes(
    votes: List<Vote>,
    userId: Int,
): PollVoteEnum {
    return votes.filter { it.userId == userId }.map { it.option }.firstOrNull()
        ?: PollVoteEnum.FALSE
}
