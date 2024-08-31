package de.simpletactics.wiki.lib.util

import de.simpletactics.wiki.lib.model.WikiAccessDeniedException
import de.simpletactics.wiki.lib.model.WikiNotFoundException
import de.simpletactics.wiki.lib.model.WikiType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Global extension functions
 */
fun (() -> Boolean).checkAccess(accessDeniedMessage: String) {
    if (this.invoke().not()) {
        throw WikiAccessDeniedException(accessDeniedMessage)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun verify(
    actualWikiType: WikiType?,
    shouldBeWikiType: WikiType,
    shouldNotBeNull: Any?,
    errorMessage: () -> String
) {

    contract {
        returns() implies (shouldNotBeNull != null)
    }

    if (actualWikiType != shouldBeWikiType || shouldNotBeNull == null) {
        throw WikiNotFoundException(errorMessage.invoke())
    }
}