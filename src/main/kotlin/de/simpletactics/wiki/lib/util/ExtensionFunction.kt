package de.simpletactics.wiki.lib.util

import de.simpletactics.wiki.lib.model.WikiAccessDeniedException

/**
 * Global extension functions
 */
fun (() -> Boolean).checkAccess(accessDeniedMessage: String) {
    if (this.invoke().not()) {
        throw WikiAccessDeniedException(accessDeniedMessage)
    }
}
