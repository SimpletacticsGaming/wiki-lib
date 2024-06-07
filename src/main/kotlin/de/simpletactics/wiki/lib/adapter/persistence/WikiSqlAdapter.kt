package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Component
import java.sql.Statement

@Component
class WikiSqlAdapter(
    private val jdbc: JdbcTemplate,
) {

    fun getWikiType(id: Int): WikiType? {
        val result = jdbc.queryForList("SELECT type FROM wiki WHERE id = %d", id)
        return if (result.size == 1 && result.first().containsKey("type")) {
            val typeAsString = result.first()["type"].toString()
            WikiType.valueOf(typeAsString)
        } else {
            null
        }
    }

    fun addToWiki(wikiType: WikiType): Int {
        val sql = "INSERT INTO wiki (type) VALUES (?)"
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        jdbc.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, wikiType.toString())
            ps
        }, keyHolder)
        return keyHolder.key?.toInt() ?: throw IllegalStateException("Failed to insert into wiki")
    }

    fun getTopic(id: Int): TopicEntity? {
        val result = jdbc.queryForList("SELECT * FROM wiki_topic WHERE id = %d", id)
        return if (result.size == 1 && result.first().containsKey("id") && result.first()
                .containsKey("topic") && result.first().containsKey("child_ids")
        ) {
            val entityAsMap = result.first()
            TopicEntity(
                entityAsMap["id"].toString().toInt(),
                entityAsMap["topic"].toString(),
                entityAsMap["child_ids"].toString().split(",").map { it.toInt() }.toList()
            )
        } else {
            null
        }
    }

    fun createTopic(topicEntity: TopicEntity): Int {
        return jdbc.update("INSERT INTO wiki_topic (id, topic) VALUES (%d, %s);", topicEntity.id, topicEntity.topic)
    }

    @Throws(IllegalStateException::class)
    fun updateTopic(topicEntity: TopicEntity): Int {
        val effectedRows = jdbc.update(
            "UPDATE wiki_topic SET topic = %s, child_ids = %s WHERE id = %d;",
            topicEntity.topic,
            topicEntity.childIds,
            topicEntity.id
        )
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update topic updated $effectedRows rows instead only 1 for id ${topicEntity.id}. Throw exception for rollback.")
    }

    fun getEntry(id: Int): EntryEntity? {
        val result = jdbc.queryForList("SELECT * FROM wiki_entry WHERE id = %d", id)
        return if (result.size == 1 && result.first().containsKey("id") && result.first()
                .containsKey("headline") && result.first().containsKey("body")
        ) {
            val entityAsMap = result.first()
            EntryEntity(
                entityAsMap["id"].toString().toInt(),
                entityAsMap["headline"].toString(),
                entityAsMap["body"].toString()
            )
        } else null
    }

    fun createEntry(entryEntity: EntryEntity): Int {
        return jdbc.update(
            "INSERT INTO wiki_entry (id, headline, body) VALUES (%d, %s, %s);",
            entryEntity.id,
            entryEntity.headline,
            entryEntity.htmlEntry
        )
    }

    @Throws(IllegalStateException::class)
    fun updateEntry(entryEntity: EntryEntity): Int {
        val effectedRows = jdbc.update(
            "UPDATE wiki_entry SET headline = %s, body = %s WHERE id = %d;",
            entryEntity.headline,
            entryEntity.htmlEntry,
            entryEntity.id
        )
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update entry updated $effectedRows rows instead only 1 for id ${entryEntity.id}. Throw exception for rollback.")
    }

    fun getTopicForChild(childId: Int): TopicEntity? {
        val result = jdbc.queryForList(
            "SELECT * FROM (SELECT id, topic, unnest(child_id) as child_id FROM wiki_topic) as topic WHERE child_id = %d",
            childId
        )
        return if (result.size == 1 && result.first().containsKey("id") && result.first()
                .containsKey("topic") && result.first().containsKey("child_id")
        ) {
            val entityAsMap = result.first()
            TopicEntity(
                entityAsMap["id"].toString().toInt(),
                entityAsMap["topic"].toString(),
                entityAsMap["child_id"].toString().split(",").map { it.toInt() }.toList()
            )
        } else null
    }
}
