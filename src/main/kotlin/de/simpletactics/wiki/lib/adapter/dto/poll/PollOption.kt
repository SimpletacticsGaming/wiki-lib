package de.simpletactics.model.poll

import com.fasterxml.jackson.annotation.JsonProperty

data class PollOption(
    @JsonProperty("uuid")
    val uuid: String,
    @JsonProperty("text")
    val text: String,
)
