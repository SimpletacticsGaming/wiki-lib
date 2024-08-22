package de.simpletactics.wiki.lib.services.port

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType

interface WikiPort {

    fun getTopic(id: Int, hasAccess: () -> Boolean): TopicEntity?

    fun createTopic(parentId: Int, topic: String, hasAccess: () -> Boolean): Int

    fun updateTopic(id: Int, topic: String, hasAccess: () -> Boolean): Int

    fun deleteTopic(id: Int, hasAccess: () -> Boolean)

    fun getEntry(id: Int, hasAccess: () -> Boolean): EntryEntity?

    fun createEntry(topicId: Int, headline: String, body: String, hasAccess: () -> Boolean): Int

    fun updateEntry(id: Int, headline: String, body: String, hasAccess: () -> Boolean): Int

    fun deleteEntry(id: Int, hasAccess: () -> Boolean)

    fun getWikiType(id: Int, hasAccess: () -> Boolean): WikiType?

}
