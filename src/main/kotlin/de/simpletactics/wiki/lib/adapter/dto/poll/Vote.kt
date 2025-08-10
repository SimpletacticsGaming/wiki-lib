package de.simpletactics.wiki.lib.adapter.dto.poll

import com.fasterxml.jackson.annotation.JsonProperty
import de.simpletactics.wiki.lib.adapter.dto.poll.PollVoteEnum

data class Vote(
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("date")
    val date: String,
    @JsonProperty("option")
    val option: PollVoteEnum,
)
