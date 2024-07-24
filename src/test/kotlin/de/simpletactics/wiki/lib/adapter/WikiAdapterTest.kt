package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testing")
class WikiAdapterTest {

    @Autowired
    private lateinit var wikiAdapter: WikiAdapter

    @Test
    fun getWikiTypeTest() {
        val wikiType = wikiAdapter.getWikiType(11)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
    }

    @Test
    fun getNoWikiTypeTest() {
        val wikiType = wikiAdapter.getWikiType(999)
        assertThat(wikiType).isNull()
    }

    @Test
    fun getTopicTest() {
        val topic = wikiAdapter.getTopic(13)
        assertThat(topic).isEqualTo(TopicEntity(13, "Thema 3", listOf(5)))
    }

    @Test
    fun createTopicTest() {
        val wikiId = wikiAdapter.createTopic(4, "New Topic 1")
        val wikiType = wikiAdapter.getWikiType(wikiId)
        val parentTopic = wikiAdapter.getTopic(4)
        val newTopic = wikiAdapter.getTopic(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
        assertThat(parentTopic).isEqualTo(TopicEntity(4, "Thema 4", listOf(15, wikiId)))
        assertThat(newTopic).isEqualTo(TopicEntity(wikiId, "New Topic 1", listOf()))
    }

    @Test
    fun updateTopicTest() {
        val wikiId = 9
        wikiAdapter.updateTopic(wikiId, "Updated Topic 2")
        val updatedTopic = wikiAdapter.getTopic(wikiId)
        assertThat(updatedTopic).isEqualTo(TopicEntity(wikiId, "Updated Topic 2", listOf()))
    }

    @Test
    fun getEntryTest() {
        val wikiType = wikiAdapter.getWikiType(14)
        val newEntry = wikiAdapter.getEntry(14)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(newEntry).isEqualTo(EntryEntity(14, "Eintrag 1", "<p>Test</p>"))
    }

    @Test
    fun createEntryTest() {
        val wikiId = wikiAdapter.createEntry(11, "New Entry 1", "My html body")
        val wikiType = wikiAdapter.getWikiType(wikiId)
        val parent = wikiAdapter.getTopic(11)
        val newEntry = wikiAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(parent).isEqualTo(TopicEntity(11, "Thema 1", listOf(14, wikiId)))
        assertThat(newEntry).isEqualTo(EntryEntity(wikiId, "New Entry 1", "My html body"))
    }

    @Test
    fun updateEntryTest() {
        val wikiId = 15
        wikiAdapter.updateEntry(wikiId, "Updated Entry 1", "My html body")
        val wikiType = wikiAdapter.getWikiType(wikiId)
        val updatedEntry = wikiAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(updatedEntry).isEqualTo(EntryEntity(wikiId, "Updated Entry 1", "My html body"))
    }
}