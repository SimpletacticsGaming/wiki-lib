package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import de.simpletactics.wiki.lib.util.checkAccess
import de.simpletactics.wiki.lib.util.verify
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

        val wikiType = wikiSqlAdapter.getWikiType(parentId)
        val parent = wikiSqlAdapter.getTopic(parentId)

        verify(
            wikiType,
            WikiType.TOPIC,
            parent
        ) { "No topic found to create a child topic with parentId $parentId" }

        val id = wikiSqlAdapter.createTopic(TopicEntity(null, topic, mutableListOf()))
        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { add(id) })
        )

        return id
    }

    @Transactional
    override fun updateTopic(id: Int, topic: String, hasAccess: () -> Boolean): Int {
        hasAccess.checkAccess("Access denied for updating topic with id $id")

        val wikiType = wikiSqlAdapter.getWikiType(id)
        val topicEntity = wikiSqlAdapter.getTopic(id)

        verify(wikiType, WikiType.TOPIC, topicEntity) { "No topic found to update with id $id" }

        return wikiSqlAdapter.updateTopic(topicEntity.copy(topic = topic))
    }

    @Transactional
    override fun deleteTopic(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting topic for id $id")

        val wikiType = wikiSqlAdapter.getWikiType(id)
        val topic = wikiSqlAdapter.getTopic(id)
        val parent = wikiSqlAdapter.getTopicForChild(id)

        verify(wikiType, WikiType.TOPIC, topic) { "No topic found to delete with id $id" }
        verify(
            wikiType,
            WikiType.TOPIC,
            parent
        ) { "Can't delete topic with id $id because no parent topic found" }

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

        val wikiType = wikiSqlAdapter.getWikiType(topicId)
        val parent = wikiSqlAdapter.getTopic(topicId)

        verify(
            wikiType,
            WikiType.TOPIC,
            parent
        ) { "Try to create entry for an undefined topic with topicId $topicId" }

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

        val wikiType = wikiSqlAdapter.getWikiType(id)
        val entity = wikiSqlAdapter.getEntry(id)

        verify(wikiType, WikiType.ENTRY, entity) { "No entry found to update with id $id" }

        return wikiSqlAdapter.updateEntry(entity.copy(headline = headline, htmlEntry = body))
    }

    @Transactional
    override fun deleteEntry(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting entry with id $id")

        val wikiType = wikiSqlAdapter.getWikiType(id)
        val parent = wikiSqlAdapter.getTopicForChild(id)

        verify(wikiType, WikiType.ENTRY, parent) { "No entry found to delete with id $id" }

        wikiSqlAdapter.updateTopic(
            parent.copy(childIds = parent.childIds.toMutableList().apply { remove(id) })
        )
        wikiSqlAdapter.deleteEntry(id)
    }

    override fun getWikiType(id: Int, hasAccess: () -> Boolean): WikiType? {
        hasAccess.checkAccess("Access denied for getting type with id $id")
        return wikiSqlAdapter.getWikiType(id)
    }

    override fun getTopicForChild(childId: Int, hasAccess: () -> Boolean): TopicEntity? {
        hasAccess.checkAccess("Access denied for getting parent topic with id $childId")
        return wikiSqlAdapter.getTopicForChild(childId)
    }
}