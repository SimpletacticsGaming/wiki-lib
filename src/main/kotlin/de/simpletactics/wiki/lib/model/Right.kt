package de.simpletactics.wiki.lib.model

data class Right(
    val read: Boolean,
    val edit: Boolean,
    val create: Boolean,
    val write: Boolean,
    val admin: Boolean,
    val mod: Boolean,
)
