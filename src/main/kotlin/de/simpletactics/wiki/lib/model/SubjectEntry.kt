package de.simpletactics.wiki.lib.model

data class SubjectEntry(
    val wikiType: WikiType = WikiType.THEMENBEREICH,
    val id: Int,
    val title: String,
    val subjectEntries: List<SubjectEntry>,
    val wikiEntries: List<WikiEntry>,
    val right: Right,
    val wikiNavigation: WikiNavigation,
)
