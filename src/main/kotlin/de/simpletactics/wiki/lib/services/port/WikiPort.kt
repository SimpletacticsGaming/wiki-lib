package de.simpletactics.wiki.lib.services.port

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType

interface WikiPort {

    fun getWikiType(id: Int): WikiType?

    fun addToWiki(wikiType: WikiType): Int

    fun getTopic(id: Int): TopicEntity?

    fun createTopic(topicEntity: TopicEntity): Int

    fun updateTopic(topicEntity: TopicEntity): Int

    fun getEntry(id: Int): EntryEntity?

    fun createEntry(entryEntity: EntryEntity): Int

    fun updateEntry(entryEntity: EntryEntity): Int

}