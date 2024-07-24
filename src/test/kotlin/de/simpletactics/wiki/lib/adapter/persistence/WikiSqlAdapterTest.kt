package de.simpletactics.wiki.lib.adapter.persistence

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
    fun getTopicTest() {
        val topic = wikiSqlAdapter.getTopic(10)
        assertThat(topic).isEqualTo(TopicEntity(10, "Thema 10", listOf(9)))
    }

    @Test
    fun createTopicTest() {
        val wikiId = wikiSqlAdapter.createTopic(TopicEntity(null, "New Topic 1", listOf()))
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val newTopic = wikiSqlAdapter.getTopic(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
        assertThat(newTopic).isEqualTo(TopicEntity(wikiId, "New Topic 1", listOf()))
    }

    @Test
    fun updateTopicTest() {
        val wikiId = 12
        wikiSqlAdapter.updateTopic(TopicEntity(wikiId, "Updated Topic 2", listOf(12)))
        val updatedTopic = wikiSqlAdapter.getTopic(wikiId)
        assertThat(updatedTopic).isEqualTo(TopicEntity(wikiId, "Updated Topic 2", listOf(12)))
    }

    @Test
    fun deleteTopic() {
        val wikiId = 3
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val topic = wikiSqlAdapter.getTopic(wikiId)
        wikiSqlAdapter.deleteTopic(wikiId)
        val deletedWikiType = wikiSqlAdapter.getWikiType(wikiId)
        val deletedTopic = wikiSqlAdapter.getTopic(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
        assertThat(topic).isEqualTo(TopicEntity(3, "Topic to delete 2", listOf()))
        assertThat(deletedWikiType).isNull()
        assertThat(deletedTopic).isNull()
    }

    @Test
    fun deleteEntry() {
        val wikiId = 18
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val entry = wikiSqlAdapter.getEntry(wikiId)
        wikiSqlAdapter.deleteEntry(wikiId)
        val deletedWikiType = wikiSqlAdapter.getWikiType(wikiId)
        val deletedEntry = wikiSqlAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(entry).isEqualTo(EntryEntity(18, "Eintrag to delete 2", "<p>Test</p>"))
        assertThat(deletedWikiType).isNull()
        assertThat(deletedEntry).isNull()
    }

    @Test
    fun getEntryTest() {
        val wikiType = wikiSqlAdapter.getWikiType(14)
        val newEntry = wikiSqlAdapter.getEntry(14)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(newEntry).isEqualTo(EntryEntity(14, "Eintrag 1", "<p>Test</p>"))
    }

    @Test
    fun createEntryTest() {
        val wikiId = wikiSqlAdapter.createEntry(EntryEntity(null, "New Entry 1", "My html body"))
        val wikiType = wikiSqlAdapter.getWikiType(wikiId)
        val newEntry = wikiSqlAdapter.getEntry(wikiId)
        assertThat(wikiType).isEqualTo(WikiType.ENTRY)
        assertThat(newEntry).isEqualTo(EntryEntity(wikiId, "New Entry 1", "My html body"))
    }

    @Test
    fun updateEntryTest() {
        val wikiId = 15
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
