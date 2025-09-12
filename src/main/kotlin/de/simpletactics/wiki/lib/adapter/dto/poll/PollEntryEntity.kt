package de.simpletactics.wiki.lib.adapter.dto.poll

import com.fasterxml.jackson.annotation.JsonProperty
import de.simpletactics.wiki.lib.adapter.dto.poll.PollOption
import de.simpletactics.wiki.lib.adapter.dto.poll.Vote

data class PollEntryEntity(
    @JsonProperty("pollOption")
    val pollOption: PollOption,
    @JsonProperty("votes")
    val votes: MutableList<Vote>,
)
