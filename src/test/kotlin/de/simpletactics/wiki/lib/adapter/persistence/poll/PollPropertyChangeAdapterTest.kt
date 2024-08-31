package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.JdbcTemplate

class PollPropertyChangeAdapterTest(
    private val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("poll property change") {

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("open poll") {
            pollPort.isPollOpen(3) { true } shouldBe true
            pollPort.isPollOpen(6) { true } shouldBe false
        }

        test("close all open polls") {
            pollPort.closeExpiredOpenPolls { true }
            pollPort.isPollOpen(2) { true } shouldBe false
            pollPort.isPollOpen(3) { true } shouldBe true
            pollPort.isPollOpen(4) { true } shouldBe true
            pollPort.isPollOpen(5) { true } shouldBe false
        }
    }
})