package de.simpletactics.wiki.lib.services

import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WikiService(
    private val wikiPort: WikiPort,
) {

    @Transactional
    fun createTopic(topic: String): Int {
        val id = wikiPort.addToWiki(WikiType.THEMENBEREICH)
        return wikiPort.createTopic(TopicEntity(id, topic, listOf()))
    }

    @Transactional
    fun updateTopic(id: Int, topic: String) {
        val entity = wikiPort.getTopic(id)
        if (entity != null) {
            val updatedEntity = entity.copy(topic = topic)
            wikiPort.updateTopic(updatedEntity)
        }
    }


}