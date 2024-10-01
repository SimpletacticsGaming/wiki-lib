package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.JdbcTemplate

class EndPollAdapterTest(
    private val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("poll") {

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("end poll") {
            val pollIdToEnd = 6
            pollPort.endPoll(pollIdToEnd) { true }

            val endedPoll = pollPort.getPoll(pollIdToEnd) { true }
            requireNotNull(endedPoll)
            with(endedPoll) {
                ended shouldBe (true)
                date shouldBe Date.getSqlDateFrom(Date.getDate())
            }
        }
    }
})