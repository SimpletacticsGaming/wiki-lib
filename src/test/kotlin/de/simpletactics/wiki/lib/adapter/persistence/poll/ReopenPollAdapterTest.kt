package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.JdbcTemplate

class ReopenPollAdapterTest(
    val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("Reopens poll") {

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("Reopen closed poll with date") {
            pollPort.reopenPoll(30, Date.getStringAsDate("2025-08-09")) { true }

            with(pollPort.getPoll(30) { true }) {
                this?.date.toString() shouldBe "2025-08-09"
                this?.ended shouldBe false
            }
        }

        test("Reopen closed poll without date") {
            pollPort.reopenPoll(30, null) { true }

            with(pollPort.getPoll(30) { true }) {
                this?.date shouldBe null
                this?.ended shouldBe false
            }
        }

        test("Don't reopen open poll") {
            pollPort.reopenPoll(2, null) { true }

            pollPort.getPoll(2) { true }?.date.toString() shouldBe "2023-10-21"
        }
    }
})