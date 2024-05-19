package de.simpletactics.wiki.lib.services

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

@Service
class WikiService(
    private val wikiPort: WikiPort,
) {

    @Transactional
    fun createTopic(topic: String): Int {
        val id = wikiPort.addToWiki(WikiType.THEMENBEREICH)
        return wikiPort.createTopic(TopicEntity(id, topic, mutableListOf()))
    }

    @Transactional
    fun updateTopic(id: Int, topic: String) {
        val entity = wikiPort.getTopic(id)
        if (entity != null) {
            wikiPort.updateTopic(entity.copy(topic = topic))
        }
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    fun createEntry(topicId: Int, headline: String, body: String) {
        val id = wikiPort.addToWiki(WikiType.STANDARDEINTRAG)
        wikiPort.createEntry(EntryEntity(id, headline, body))
        val parent = wikiPort.getTopic(id)
        if (parent != null) {
            val childIds = parent.childIds.toMutableList()
            childIds.add(id)
            wikiPort.updateTopic(parent.copy(childIds = childIds))
        } else {
            throw IllegalArgumentException("Try to create entry with entryId $id for an undefined topic with topicId $topicId")
        }
    }

    @Transactional
    fun updateEntry(id: Int, headline: String, body: String) {
        val entity = wikiPort.getEntry(id)
        if (entity != null) {
            wikiPort.updateEntry(entity.copy(headline = headline, htmlEntry = body))
        }
    }


}