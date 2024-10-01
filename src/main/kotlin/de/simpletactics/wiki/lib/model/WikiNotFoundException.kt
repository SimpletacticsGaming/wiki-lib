package de.simpletactics.wiki.lib.model

class WikiNotFoundException(message: String) : Exception(message)

class WikiAccessDeniedException(message: String) : Exception(message)
