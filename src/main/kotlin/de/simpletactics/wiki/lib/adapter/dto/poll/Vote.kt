package de.simpletactics.model.poll

import com.fasterxml.jackson.annotation.JsonProperty

data class Vote(
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("date")
    val date: String,
    @JsonProperty("option")
    val option: PollVoteEnum,
)
