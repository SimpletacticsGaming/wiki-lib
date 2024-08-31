package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.JdbcTemplate

class DeletePollAdapterTest(
    private val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("poll") {

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("delete poll") {
            val pollIdToDelete = 6
            val fetchedPoll = pollPort.getPoll(pollIdToDelete) { true }
            pollPort.deletePoll(pollIdToDelete) { true }

            fetchedPoll shouldNotBe null
            pollPort.getPoll(pollIdToDelete) { true } shouldBe null
        }
    }
})