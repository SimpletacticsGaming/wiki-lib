package de.simpletactics.wiki.lib.model

data class WikiEntry(
    val wikiType: WikiType,
    val id: Int,
    val topic: String,
    val htmlEntry: String,
    val right: Right,
    val wikiNavigation: WikiNavigation,
)
