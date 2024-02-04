package de.simpletactics.wiki.lib.services.port

import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType

interface WikiPort {

    fun addToWiki(wikiType: WikiType): Int

    fun createTopic(topicEntity: TopicEntity): Int

    fun updateTopic(topicEntity: TopicEntity): Int

    fun updateTopic(id: Int, topic: String): Int

}