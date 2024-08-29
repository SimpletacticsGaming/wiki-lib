package de.simpletactics.wiki.lib.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

val jsonObjectMapper: ObjectMapper = ObjectMapper().registerModule(kotlinModule())