package de.simpletactics.wiki.lib.adapter.dto.poll

import com.fasterxml.jackson.annotation.JsonProperty
import de.simpletactics.model.poll.PollOption
import de.simpletactics.model.poll.Vote

data class PollEntryEntity(
    @JsonProperty("pollOption")
    val pollOption: PollOption,
    @JsonProperty("votes")
    val votes: MutableList<Vote>,
)
