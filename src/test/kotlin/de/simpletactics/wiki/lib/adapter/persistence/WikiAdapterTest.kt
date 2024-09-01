package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.WikiAdapter
import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.model.WikiAccessDeniedException
import de.simpletactics.wiki.lib.model.WikiNotFoundException
import de.simpletactics.wiki.lib.model.WikiType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.JdbcTemplate

class WikiAdapterTest(
    private val wikiAdapter: WikiAdapter,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("wiki type") {
        beforeTest {
            executeSql("wiki_type.sql")
        }

        test("get") {
            wikiAdapter.getWikiType(11) { true } shouldBe WikiType.TOPIC
            wikiAdapter.getWikiType(14) { true } shouldBe WikiType.ENTRY
            wikiAdapter.getWikiType(5) { true } shouldBe WikiType.POLL
        }

        test("get no type") {
            wikiAdapter.getWikiType(99) { true } shouldBe null
        }
    }

    context("topic") {

        beforeTest {
            executeSql("wiki_topic.sql")
        }

        test("get") {
            wikiAdapter.getTopic(13) { true } shouldBe TopicEntity(13, "Thema 3", listOf(5))
            wikiAdapter.getTopic(99) { true } shouldBe null
        }

        test("create") {
            val wikiId = wikiAdapter.createTopic(4, "New Topic 1") { true }

            wikiAdapter.getWikiType(wikiId) { true } shouldBe WikiType.TOPIC
            wikiAdapter.getTopic(4) { true } shouldBe TopicEntity(
                4,
                "Thema 4",
                listOf(15, wikiId)
            )
            wikiAdapter.getTopic(wikiId) { true } shouldBe TopicEntity(
                wikiId,
                "New Topic 1",
                listOf()
            )
        }

        test("update") {
            val wikiId = 9
            wikiAdapter.updateTopic(wikiId, "Updated Topic 2") { true }
            wikiAdapter.getTopic(wikiId) { true } shouldBe TopicEntity(
                wikiId,
                "Updated Topic 2",
                listOf()
            )
        }

        test("delete") {
            val wikiId = 2
            val parentId = 20
            val topic = wikiAdapter.getTopic(wikiId) { true }

            wikiAdapter.deleteTopic(wikiId) { true }

            wikiAdapter.getTopic(parentId) { true } shouldBe TopicEntity(
                parentId,
                "Topic to delete 2",
                listOf(21)
            )
            wikiAdapter.getWikiType(wikiId) { true } shouldBe null
            wikiAdapter.getTopic(wikiId) { true } shouldBe null

            requireNotNull(topic)
            with(topic) {
                childIds shouldHaveSize 2
                childIds.forEach { wikiAdapter.getEntry(it) { true } shouldBe null }
            }
        }
    }

    context("entry") {

        beforeTest {
            executeSql("wiki_entry.sql")
        }

        test("get") {
            wikiAdapter.getWikiType(14) { true } shouldBe WikiType.ENTRY
            wikiAdapter.getEntry(14) { true } shouldBe EntryEntity(
                14,
                "Eintrag 1",
                "<p>Test</p>"
            )

            wikiAdapter.getEntry(99) { true } shouldBe null
        }

        test("create") {
            val wikiId = wikiAdapter.createEntry(11, "New Entry 1", "My html body") { true }
            wikiAdapter.getWikiType(wikiId) { true } shouldBe WikiType.ENTRY
            wikiAdapter.getTopic(11) { true } shouldBe TopicEntity(
                11,
                "Thema 1",
                listOf(14, wikiId)
            )
            wikiAdapter.getEntry(wikiId) { true } shouldBe EntryEntity(
                wikiId,
                "New Entry 1",
                "My html body"
            )
        }

        test("update") {
            val wikiId = 15
            wikiAdapter.updateEntry(wikiId, "Updated Entry 1", "My html body") { true }
            wikiAdapter.getWikiType(wikiId) { true } shouldBe WikiType.ENTRY
            wikiAdapter.getEntry(wikiId) { true } shouldBe EntryEntity(
                wikiId,
                "Updated Entry 1",
                "My html body"
            )
        }

        test("delete") {
            val wikiId = 7
            val parentId = 8
            wikiAdapter.deleteEntry(wikiId) { true }
            wikiAdapter.getWikiType(wikiId) { true } shouldBe null
            wikiAdapter.getTopic(parentId) { true } shouldBe TopicEntity(
                parentId,
                "Topic with child delete",
                listOf()
            )
            wikiAdapter.getEntry(wikiId) { true } shouldBe null
        }
    }

    context("exception") {
        beforeTest {
            executeSql("wiki_exception.sql")
        }

        test("delete entry with topic id") {
            val topicId = 11
            shouldThrow<WikiNotFoundException> {
                wikiAdapter.deleteEntry(topicId) { true }
            } shouldBe WikiNotFoundException("No entry found to delete with id $topicId")
        }

        test("delete topic with wrong topic id") {
            val topicId = 19
            shouldThrow<WikiNotFoundException> {
                wikiAdapter.deleteTopic(topicId) { true }
            } shouldBe WikiNotFoundException("No topic found to delete with id $topicId")
        }

        test("update entry") {
            val entryId = 999
            shouldThrow<WikiNotFoundException> {
                wikiAdapter.updateEntry(entryId, "Updated Entry 1", "My html body") { true }
            } shouldBe WikiNotFoundException("No entry found to update with id $entryId")
        }

        test("update topic") {
            val topicId = 999
            shouldThrow<WikiNotFoundException> {
                wikiAdapter.updateTopic(topicId, "Updated Topic 2") { true }
            } shouldBe WikiNotFoundException("No topic found to update with id $topicId")
        }

        test("access denied test") {
            shouldThrow<WikiAccessDeniedException> {
                wikiAdapter.getEntry(23) { false }
            } shouldBe WikiAccessDeniedException("Access denied for getting entry with id 23")
        }

    }
})
