package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import kotlin.jvm.Throws

@Component
class WikiAdapter(
        private val jdbc: JdbcTemplate,
) : WikiPort {
    override fun getWikiType(id: Int): WikiType? {
       val result = jdbc.queryForList("SELECT type FROM wiki WHERE id = %d", id)
       return if (result.size == 1 && result.first().containsKey("type")) {
           val typeAsString = result.first()["type"].toString()
           WikiType.valueOf(typeAsString)
       } else {
           null
       }
    }

    override fun addToWiki(wikiType: WikiType): Int {
        // TODO: Id generation
        return jdbc.update("INSERT INTO wiki (id, type) VALUES (%d,%s);", 0, wikiType)
    }

    override fun getTopic(id: Int): TopicEntity? {
       val result = jdbc.queryForList("SELECT * FROM wiki_topic WHERE id = %d", id)
        return if (result.size == 1 && result.first().containsKey("id") && result.first().containsKey("topic") && result.first().containsKey("child_ids")) {
            val entityAsMap = result.first()
            TopicEntity(
                    entityAsMap["id"].toString().toInt(),
                    entityAsMap["topic"].toString(),
                    entityAsMap["child_ids"].toString().split(",").map { it.toInt() }.toMutableList()
            )
        } else {
            null
        }
    }

    override fun createTopic(topicEntity: TopicEntity): Int {
        return jdbc.update("INSERT INTO wiki_topic (id, topic) VALUES (%d, %s);", topicEntity.id, topicEntity.topic)
    }

    @Throws(IllegalStateException::class)
    override fun updateTopic(topicEntity: TopicEntity): Int {
        val effectedRows = jdbc.update("UPDATE wiki_topic SET topic = %s, child_ids = %s WHERE id = %d;", topicEntity.topic, topicEntity.childIds, topicEntity.id)
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update topic updated $effectedRows rows instead only 1 for id ${topicEntity.id}. Throw exception for rollback.")
    }

    override fun getEntry(id: Int): EntryEntity? {
        val result = jdbc.queryForList("SELECT * FROM wiki_entry WHERE id = %d", id)
        return if (result.size == 1 && result.first().containsKey("id") && result.first().containsKey("headline") && result.first().containsKey("body")) {
            val entityAsMap = result.first()
            EntryEntity(
                    entityAsMap["id"].toString().toInt(),
                    entityAsMap["headline"].toString(),
                    entityAsMap["body"].toString()
            )
        } else null
    }

    override fun createEntry(entryEntity: EntryEntity): Int {
        return jdbc.update("INSERT INTO wiki_entry (id, headline, body) VALUES (%d, %s, %s);", entryEntity.id, entryEntity.headline, entryEntity.htmlEntry)
    }

    @Throws(IllegalStateException::class)
    override fun updateEntry(entryEntity: EntryEntity): Int {
        val effectedRows = jdbc.update("UPDATE wiki_entry SET headline = %s, body = %s WHERE id = %d;", entryEntity.headline, entryEntity.htmlEntry, entryEntity.id)
        return if (effectedRows == 1) effectedRows else
            throw IllegalStateException("Update entry updated $effectedRows rows instead only 1 for id ${entryEntity.id}. Throw exception for rollback.")
    }

    override fun getTopicForChild(childId: Int): TopicEntity? {
        val result = jdbc.queryForList("SELECT * FROM (SELECT id, topic, unnest(child_id) as child_id FROM wiki_topic) as topic WHERE child_id = %d", childId)
        return if (result.size == 1 && result.first().containsKey("id") && result.first().containsKey("topic") && result.first().containsKey("child_id")) {
            val entityAsMap = result.first()
            TopicEntity(
                    entityAsMap["id"].toString().toInt(),
                    entityAsMap["topic"].toString(),
                    entityAsMap["child_id"].toString().split(",").map { it.toInt() }.toMutableList()
            )
        } else null
    }
}