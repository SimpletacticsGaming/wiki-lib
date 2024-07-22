package de.simpletactics.wiki.lib.adpater.persistence

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testing")
class WikiSqlAdapterTest {

    @Autowired
    private lateinit var wikiSqlAdapter: WikiSqlAdapter

    @Test
    fun getWikiTypeTest() {
        val wikiType = wikiSqlAdapter.getWikiType(11)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
    }

    @Test
    fun getNoWikiTypeTest() {
        val wikiType = wikiSqlAdapter.getWikiType(999)
        assertThat(wikiType).isNull()
    }

    @Test
    fun addToWiki() {
        val wikiId = wikiSqlAdapter.addToWiki(WikiType.TOPIC)
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
    }

    @Test
    fun getTopicTest() {
        val topic = wikiSqlAdapter.getTopic(11)
        assertThat(topic).isEqualTo(TopicEntity(11, "Thema 1", listOf(14)))
    }

    @Test
    fun createTopicTest() {
        val wikiId = wikiSqlAdapter.addToWiki(WikiType.TOPIC)
        wikiSqlAdapter.createTopic(TopicEntity(wikiId, "New Topic 1", listOf()))
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val newTopic = wikiSqlAdapter.getTopic(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
        assertThat(newTopic).isEqualTo(TopicEntity(wikiId, "New Topic 1", listOf()))
    }

    @Test
    fun updateTopicTest() {
        val wikiId = 12
        wikiSqlAdapter.updateTopic(TopicEntity(wikiId,"Updated Topic 2", listOf(12)))
        val updatedTopic = wikiSqlAdapter.getTopic(wikiId)
        assertThat(updatedTopic).isEqualTo(TopicEntity(wikiId, "Updated Topic 2", listOf(12)))
    }

    @Test
    fun getEntryTest() {
        val wikiType = wikiSqlAdapter.getWikiType(14)
        val newEntry = wikiSqlAdapter.getEntry(14)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(newEntry).isEqualTo(EntryEntity(14, "Eintrag 1", "<p>Test</p>"))
    }

    @Test
    @Disabled
    fun createEntryTest() {
        val wikiId = wikiSqlAdapter.addToWiki(WikiType.ENTRY)
        wikiSqlAdapter.createEntry(EntryEntity(wikiId, "New Entry 1", "My html body"))
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val newEntry = wikiSqlAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(newEntry).isEqualTo(EntryEntity(wikiId, "New Entry 1", "My html body"))
    }

    @Test
    fun updateEntryTest() {
        val wikiId = 24
        wikiSqlAdapter.updateEntry(EntryEntity(wikiId, "Updated Entry 1", "My html body"))
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val updatedEntry = wikiSqlAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(updatedEntry).isEqualTo(EntryEntity(wikiId, "Updated Entry 1", "My html body"))
    }

    @Test
    fun getTopicForChildIdTest() {
        val topic = wikiSqlAdapter.getTopicForChild(11)
        assertThat(topic).isEqualTo(TopicEntity(0, "Startseite", listOf(11, 12, 13)))
    }

}
