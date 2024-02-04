package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class WikiAdapter(
    private val jdbc: JdbcTemplate,
): WikiPort {

    override fun addToWiki(wikiType: WikiType): Int {
        // TODO: Id generation
        return jdbc.update("INSERT INTO wiki (id, type) VALUES (%d,%s);", 0, wikiType)
    }

    override fun createTopic(topicEntity: TopicEntity): Int {
        return jdbc.update("INSERT INTO wiki_topic (id, topic) VALUES (%d, %s);", topicEntity.id, topicEntity.topic)
    }

    override fun updateTopic(topicEntity: TopicEntity): Int {
        val effectedRows = jdbc.update("UPDATE wiki_topic SET topic = %s WHERE id = %d;", topicEntity.topic, topicEntity.id)
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update topic updated $effectedRows rows instead only 1 for id ${topicEntity.id}. Throw exception for rollback.")
    }

    override fun createEntry(entryEntity: EntryEntity): Int {
        return jdbc.update("INSERT INTO wiki_entry (id, headline, body) VALUES (%d, %s, %s);", entryEntity.id, entryEntity.headline, entryEntity.htmlEntry)
    }

    override fun updateEntry(entryEntity: EntryEntity): Int {
        val effectedRows = jdbc.update("UPDATE wiki_entry SET headline = %s, body = %s WHERE id = %d;", entryEntity.headline, entryEntity.htmlEntry, entryEntity.id)
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update entry updated $effectedRows rows instead only 1 for id ${entryEntity.id}. Throw exception for rollback.")
    }
}