package de.simpletactics.wiki.lib.services.port

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import org.springframework.data.relational.core.sql.In

interface WikiPort {

    fun getTopic(id: Int): TopicEntity?

    fun createTopic(topic: String): Int

    fun updateTopic(id: Int, topic: String): Int

    fun getEntry(id: Int): EntryEntity?

    fun createEntry(topicId: Int, headline: String, body: String): Int

    fun updateEntry(id: Int, headline: String, body: String): Int

    fun getWikiType(id: Int): WikiType?

}
