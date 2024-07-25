package de.simpletactics.wiki.lib.model

data class WikiNavigation(
    val id: Int,
    val title: String,
    val subjectEntries: List<SubjectEntry>,
    val wikiEntries: List<WikiEntry>,
)
