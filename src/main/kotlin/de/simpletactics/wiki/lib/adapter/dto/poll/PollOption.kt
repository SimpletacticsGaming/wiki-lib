package de.simpletactics.wiki.lib.adapter.dto.poll

import com.fasterxml.jackson.annotation.JsonProperty

data class PollOption(
    @JsonProperty("uuid")
    val uuid: String,
    @JsonProperty("text")
    val text: String,
)
