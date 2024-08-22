package de.simpletactics.wiki.lib.adapter.persistence.mapper

import de.simpletactics.model.poll.PollVoteEnum
import de.simpletactics.model.poll.Vote
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollModel


fun PollEntity.toModel(): PollModel {
    return PollModel(
        this.id,
        this.question,
        this.description,
        this.pollEntries,
        this.ended,
        if (this.date != null) Date.getZoneDateTimeFrom(this.date) else null,
    )
}

fun PollModel.toEntity(): PollEntity {
    return PollEntity(
        this.id,
        this.question,
        this.description,
        this.pollEntries,
        this.ended,
        if (this.date != null) Date.getSqlDateFrom(this.date) else null,
    )
}

private fun getOptionSelectionFromVotes(
    votes: List<Vote>,
    userId: Int,
): PollVoteEnum {
    return votes.filter { it.userId == userId }.map { it.option }.firstOrNull()
        ?: PollVoteEnum.FALSE
}
