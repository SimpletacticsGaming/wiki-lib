package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.mapper.EntryMapper
import de.simpletactics.wiki.lib.adapter.persistence.mapper.IdMapper
import de.simpletactics.wiki.lib.adapter.persistence.mapper.TopicRowMapper
import de.simpletactics.wiki.lib.model.WikiType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class WikiSqlAdapter(
    private val jdbc: JdbcTemplate,
) {

    fun getWikiType(id: Int): WikiType? {
        val result = jdbc.queryForList("SELECT type FROM wiki WHERE id = ?", id)
        return if (result.size == 1 && result.first().containsKey("type")) {
            val typeAsString = result.first()["type"].toString()
            WikiType.valueOf(typeAsString)
        } else {
            null
        }
    }

    fun getTopic(id: Int): TopicEntity? {
        val result = jdbc.query("SELECT * FROM wiki_topic WHERE id = ?", TopicRowMapper(), id)
        return if (result.size == 1) {
            result.first()
        } else {
            null
        }
    }

    fun createTopic(topicEntity: TopicEntity): Int {
        return jdbc.query("INSERT INTO wiki_topic (topic) VALUES (?) RETURNING id;", IdMapper(), topicEntity.topic)
            .first()
    }

    @Throws(IllegalStateException::class)
    fun updateTopic(topicEntity: TopicEntity): Int {
        val effectedRows = jdbc.update(
            "UPDATE wiki_topic SET topic = ?, child_id = ? WHERE id = ?;",
            topicEntity.topic,
            topicEntity.childIds.toIntArray(),
            topicEntity.id
        )
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update topic updated $effectedRows rows instead only 1 for id ${topicEntity.id}. Throw exception for rollback.")
    }

    fun getEntry(id: Int): EntryEntity? {
        val result = jdbc.query("SELECT * FROM wiki_entry WHERE id = ?", EntryMapper(), id)
        return if (result.size == 1) {
            result.first()
        } else {
            null
        }
    }

    fun createEntry(entryEntity: EntryEntity): Int {
        return jdbc.query(
            "INSERT INTO wiki_entry (headline, body) VALUES (?, ?) RETURNING id;",
            IdMapper(),
            entryEntity.headline,
            entryEntity.htmlEntry
        ).first()
    }

    @Throws(IllegalStateException::class)
    fun updateEntry(entryEntity: EntryEntity): Int {
        val effectedRows = jdbc.update(
            "UPDATE wiki_entry SET headline = ?, body = ? WHERE id = ?;",
            entryEntity.headline,
            entryEntity.htmlEntry,
            entryEntity.id
        )
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update entry updated $effectedRows rows instead only 1 for id ${entryEntity.id}. Throw exception for rollback.")
    }

    fun getTopicForChild(childId: Int): TopicEntity? {
        val result = jdbc.query(
            "SELECT * FROM wiki_topic WHERE ? = ANY(child_id)",
            TopicRowMapper(),
            childId
        )
        return if (result.size == 1) {
            result.first()
        } else {
            null
        }
    }
}
