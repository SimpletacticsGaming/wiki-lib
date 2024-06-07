package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

@Component
class WikiAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
): WikiPort {

    override fun getTopic(id: Int): TopicEntity? {
       return wikiSqlAdapter.getTopic(id)
    }

    @Transactional
    override fun createTopic(topic: String): Int {
        val id = wikiSqlAdapter.addToWiki(WikiType.THEMENBEREICH)
        return wikiSqlAdapter.createTopic(TopicEntity(id, topic, mutableListOf()))
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun updateTopic(id: Int, topic: String): Int {
        val entity = wikiSqlAdapter.getTopic(id)
        return if (entity != null) {
            wikiSqlAdapter.updateTopic(entity.copy(topic = topic))
        } else {
            throw IllegalArgumentException("No topic found to update with id $id")
        }
    }

    override fun getEntry(id: Int): EntryEntity? {
        return wikiSqlAdapter.getEntry(id)
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun createEntry(topicId: Int, headline: String, body: String): Int {
        val id = wikiSqlAdapter.addToWiki(WikiType.STANDARDEINTRAG)
        wikiSqlAdapter.createEntry(EntryEntity(id, headline, body))
        val parent = wikiSqlAdapter.getTopic(id)
        return if (parent != null) {
            val childIds = parent.childIds.toMutableList()
            childIds.add(id)
            wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
        } else {
            throw IllegalArgumentException("Try to create entry with entryId $id for an undefined topic with topicId $topicId")
        }
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun updateEntry(id: Int, headline: String, body: String): Int {
        val entity = wikiSqlAdapter.getEntry(id)
        return if (entity != null) {
            wikiSqlAdapter.updateEntry(entity.copy(headline = headline, htmlEntry = body))
        } else {
            throw IllegalArgumentException("No entry found to update with id $id")
        }
    }

    override fun getWikiType(id: Int): WikiType? {
       return wikiSqlAdapter.getWikiType(id)
    }


}
