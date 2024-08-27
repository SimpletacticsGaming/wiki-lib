package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiNotFoundException
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import de.simpletactics.wiki.lib.util.checkAccess
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WikiAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
) : WikiPort {

    override fun getTopic(id: Int, hasAccess: () -> Boolean): TopicEntity? {
        hasAccess.checkAccess("Access denied to get topic for id $id")
        return wikiSqlAdapter.getTopic(id)
    }

    @Transactional
    override fun createTopic(parentId: Int, topic: String, hasAccess: () -> Boolean): Int {
        hasAccess.checkAccess("Access denied for creating topic for parentId $parentId")

        val parent = wikiSqlAdapter.getTopic(parentId)

        check(wikiSqlAdapter.getWikiType(parentId) == WikiType.TOPIC && parent != null) {
            throw WikiNotFoundException("No topic found to create a child topic with parentId $parentId")
        }

        val id = wikiSqlAdapter.createTopic(TopicEntity(null, topic, mutableListOf()))
        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { add(id) })
        )

        return id
    }

    @Transactional
    override fun updateTopic(id: Int, topic: String, hasAccess: () -> Boolean): Int {
        hasAccess.checkAccess("Access denied for updating topic with id $id")

        val topicEntity = wikiSqlAdapter.getTopic(id)

        check(wikiSqlAdapter.getWikiType(id) == WikiType.TOPIC && topicEntity != null) {
            throw WikiNotFoundException("No topic found to update with id $id")
        }

        return wikiSqlAdapter.updateTopic(topicEntity.copy(topic = topic))
    }

    @Transactional
    override fun deleteTopic(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting topic for id $id")

        val topic = wikiSqlAdapter.getTopic(id)
        val parent = wikiSqlAdapter.getTopicForChild(id)

        check(wikiSqlAdapter.getWikiType(id) == WikiType.TOPIC && topic != null && parent != null) {
            throw WikiNotFoundException("No topic found to delete with id $id")
        }

        topic.childIds.forEach { wikiSqlAdapter.deleteEntry(it) }
        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { remove(id) })
        )
        wikiSqlAdapter.deleteTopic(id)
    }

    override fun getEntry(id: Int, hasAccess: () -> Boolean): EntryEntity? {
        hasAccess.checkAccess("Access denied for getting entry with id $id")
        return wikiSqlAdapter.getEntry(id)
    }

    @Transactional
    override fun createEntry(
        topicId: Int,
        headline: String,
        body: String,
        hasAccess: () -> Boolean
    ): Int {
        hasAccess.checkAccess("Access denied for creating entry with id $topicId")

        val parent = wikiSqlAdapter.getTopic(topicId)

        check(wikiSqlAdapter.getWikiType(topicId) == WikiType.TOPIC && parent != null) {
            throw WikiNotFoundException("Try to create entry for an undefined topic with topicId $topicId")
        }

        val entryId = wikiSqlAdapter.createEntry(EntryEntity(null, headline, body))
        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { add(entryId) })
        )

        return entryId
    }

    @Transactional
    override fun updateEntry(
        id: Int,
        headline: String,
        body: String,
        hasAccess: () -> Boolean
    ): Int {
        hasAccess.checkAccess("Access denied for updating entry with id $id")

        val entity = wikiSqlAdapter.getEntry(id)

        check(wikiSqlAdapter.getWikiType(id) == WikiType.ENTRY && entity != null) {
            throw WikiNotFoundException("No entry found to update with id $id")
        }

        return wikiSqlAdapter.updateEntry(entity.copy(headline = headline, htmlEntry = body))
    }

    @Transactional
    override fun deleteEntry(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting entry with id $id")

        val parent = wikiSqlAdapter.getTopicForChild(id)

        check(wikiSqlAdapter.getWikiType(id) == WikiType.ENTRY && parent != null) {
            throw WikiNotFoundException("No entry found to delete with id $id")
        }

        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { remove(id) })
        )
        wikiSqlAdapter.deleteEntry(id)
    }

    override fun getWikiType(id: Int, hasAccess: () -> Boolean): WikiType? {
        hasAccess.checkAccess("Access denied for getting type with id $id")
        return wikiSqlAdapter.getWikiType(id)
    }

}
