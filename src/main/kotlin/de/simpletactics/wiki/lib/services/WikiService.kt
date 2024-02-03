package de.simpletactics.wiki.lib.services

import de.simpletactics.wiki.lib.services.port.WikiPort
import org.springframework.stereotype.Service

@Service
class WikiService(
    private val wikiPort: WikiPort,
) {

    fun createTopic(topic: String): Int {
        // Add to Zentraltabelle (wiki) id + type
        // Add to Themenbereich (wiki_topic) id + thema
        return 0
    }

    fun updateTopic(id: Int, topic: String) {
         // Update entry in wiki_topic
    }

}