package de.simpletactics.wiki.lib.adapter.dto

data class TopicEntity(
    val id: Int,
    val topic: String,
    val childIds: List<Int>,
)
