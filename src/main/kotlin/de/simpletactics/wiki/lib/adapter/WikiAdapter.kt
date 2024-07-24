package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WikiAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
) : WikiPort {

    override fun getTopic(id: Int): TopicEntity? {
        return wikiSqlAdapter.getTopic(id)
    }

    @Transactional
    override fun createTopic(parentId: Int, topic: String): Int {
        val wikiType = wikiSqlAdapter.getWikiType(parentId)
        if (wikiType == WikiType.TOPIC) {
            val parent = wikiSqlAdapter.getTopic(parentId)
            return if (parent != null) {
                val id = wikiSqlAdapter.createTopic(TopicEntity(null, topic, mutableListOf()))
                val childIds = parent.childIds.toMutableList()
                childIds.add(id)
                wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                id
            } else {
                throw IllegalArgumentException("No topic found to create a child topic with parentId $parentId")
            }
        } else {
            throw IllegalArgumentException("No topic found to create a child topic with parentId $parentId")
        }
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun updateTopic(id: Int, topic: String): Int {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.TOPIC) {
            val topicEntity = wikiSqlAdapter.getTopic(id)
            return if (topicEntity != null) {
                wikiSqlAdapter.updateTopic(topicEntity.copy(topic = topic))
            } else {
                throw IllegalArgumentException("No topic found to update with id $id")
            }
        } else {
            throw IllegalArgumentException("No topic found to update with id $id")
        }
    }

    @Transactional
    override fun deleteTopic(id: Int) {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.TOPIC) {
            val topic = wikiSqlAdapter.getTopic(id)
            if (topic != null) {
                val parent = wikiSqlAdapter.getTopicForChild(id)
                if (parent != null) {
                    topic.childIds.forEach { wikiSqlAdapter.deleteEntry(it) }
                    val childIds = parent.childIds.toMutableList()
                    childIds.remove(id)
                    wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                    wikiSqlAdapter.deleteTopic(id)
                }
            }
        }
    }

    override fun getEntry(id: Int): EntryEntity? {
        return wikiSqlAdapter.getEntry(id)
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun createEntry(topicId: Int, headline: String, body: String): Int {
        val parentWikiType = wikiSqlAdapter.getWikiType(topicId)
        if (parentWikiType == WikiType.TOPIC) {
            val parent = wikiSqlAdapter.getTopic(topicId)
            return if (parent != null) {
                val entryId = wikiSqlAdapter.createEntry(EntryEntity(null, headline, body))
                val childIds = parent.childIds.toMutableList()
                childIds.add(entryId)
                wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                entryId
            } else {
                throw IllegalArgumentException("Try to create entry for an undefined topic with topicId $topicId")
            }
        } else {
            throw IllegalArgumentException("Try to create entry for an undefined topic with topicId $topicId")
        }
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun updateEntry(id: Int, headline: String, body: String): Int {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.ENTRY) {
            val entity = wikiSqlAdapter.getEntry(id)
            return if (entity != null) {
                wikiSqlAdapter.updateEntry(entity.copy(headline = headline, htmlEntry = body))
            } else {
                throw IllegalArgumentException("No entry found to update with id $id")
            }
        } else {
            throw IllegalArgumentException("No entry found to update with id $id")
        }
    }

    @Transactional
    override fun deleteEntry(id: Int) {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.ENTRY) {
            val parent = wikiSqlAdapter.getTopicForChild(id)
            if (parent != null) {
                val childIds = parent.childIds.toMutableList()
                childIds.remove(id)
                wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                wikiSqlAdapter.deleteEntry(id)
            }
        }
    }

    override fun getWikiType(id: Int): WikiType? {
        return wikiSqlAdapter.getWikiType(id)
    }


}
