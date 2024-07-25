package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiException
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
            if (parent != null) {
                val id = wikiSqlAdapter.createTopic(TopicEntity(null, topic, mutableListOf()))
                val childIds = parent.childIds.toMutableList()
                childIds.add(id)
                wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                return id
            }
        }
        throw WikiException("No topic found to create a child topic with parentId $parentId")
    }

    @Transactional
    override fun updateTopic(id: Int, topic: String): Int {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.TOPIC) {
            val topicEntity = wikiSqlAdapter.getTopic(id)
            if (topicEntity != null) {
                return wikiSqlAdapter.updateTopic(topicEntity.copy(topic = topic))
            }
        }
        throw WikiException("No topic found to update with id $id")
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
                    return
                }
            }
        }
        throw WikiException("No topic found to delete with id $id")
    }

    override fun getEntry(id: Int): EntryEntity? {
        return wikiSqlAdapter.getEntry(id)
    }

    @Transactional
    override fun createEntry(topicId: Int, headline: String, body: String): Int {
        val parentWikiType = wikiSqlAdapter.getWikiType(topicId)
        if (parentWikiType == WikiType.TOPIC) {
            val parent = wikiSqlAdapter.getTopic(topicId)
            if (parent != null) {
                val entryId = wikiSqlAdapter.createEntry(EntryEntity(null, headline, body))
                val childIds = parent.childIds.toMutableList()
                childIds.add(entryId)
                wikiSqlAdapter.updateTopic(parent.copy(childIds = childIds))
                return entryId
            }
        }
        throw WikiException("Try to create entry for an undefined topic with topicId $topicId")
    }

    @Transactional
    override fun updateEntry(id: Int, headline: String, body: String): Int {
        val wikiType = wikiSqlAdapter.getWikiType(id)
        if (wikiType == WikiType.ENTRY) {
            val entity = wikiSqlAdapter.getEntry(id)
            if (entity != null) {
                return wikiSqlAdapter.updateEntry(entity.copy(headline = headline, htmlEntry = body))
            }
        }
        throw WikiException("No entry found to update with id $id")
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
                return
            }
        }
        throw WikiException("No entry found to delete with id $id")
    }

    override fun getWikiType(id: Int): WikiType? {
        return wikiSqlAdapter.getWikiType(id)
    }

}
