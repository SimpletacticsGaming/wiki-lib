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
        return jdbc.update("INSERT INTO wiki (id, type) VALUES ($0, $wikiType);")
    }

    override fun createTopic(topicEntity: TopicEntity): Int {
        return jdbc.update("INSERT INTO wiki_topic (id, topic) VALUES (${topicEntity.id}, '${topicEntity.topic}');")
    }

    override fun updateTopic(topicEntity: TopicEntity): Int {
        val effectedRows = jdbc.update("UPDATE wiki_topic SET topic = '${topicEntity.topic}' WHERE id = ${topicEntity.id};")
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update topic updated $effectedRows rows instead only 1 for id ${topicEntity.id}. Throw exception for rollback.")
    }

    override fun createEntry(entryEntity: EntryEntity): Int {
        TODO("Not yet implemented")
    }

    override fun updateEntry(entryEntity: EntryEntity): Int {
        TODO("Not yet implemented")
    }
}