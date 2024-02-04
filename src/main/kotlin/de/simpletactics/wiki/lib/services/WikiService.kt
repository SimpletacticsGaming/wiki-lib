package de.simpletactics.wiki.lib.services

import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Service

@Service
class WikiService(
    private val wikiPort: WikiPort,
) {

    fun createTopic(topic: String): Int {
        // Add to Zentraltabelle (wiki) id + type
        val id = wikiPort.addToWiki(WikiType.THEMENBEREICH)
        // Add to Themenbereich (wiki_topic) id + thema
        return wikiPort.createTopic(id, topic)
    }

    fun updateTopic(id: Int, topic: String) {
        wikiPort.updateTopic(id, topic)
    }


}