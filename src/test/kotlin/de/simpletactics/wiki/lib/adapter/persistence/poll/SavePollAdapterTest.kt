package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.model.poll.PollOption
import de.simpletactics.model.poll.PollVoteEnum
import de.simpletactics.model.poll.Vote
import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollModel
import de.simpletactics.wiki.lib.adapter.persistence.mapper.toEntity
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

class SavePollAdapterTest(
    val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    val mockedNotPresentPollId = 999
    val mockedPresentDatabasePollId = 5

    val pollModel = PollModel(
        null,
        "This is a test",
        "",
        listOf(
            PollEntryEntity(
                PollOption("ddb11436-bdc8-4488-87f6-fsdfsd", "testCase"),
                mutableListOf(
                    Vote(
                        1,
                        Date.getDateAsString(),
                        PollVoteEnum.FALSE,
                    )
                )
            )
        ),
        false,
        null,
    )

    val mockedInDatabasePollModel = PollModel(
        mockedPresentDatabasePollId,
        "Frage 1",
        "Beschreibung 1",
        listOf(
            PollEntryEntity(
                PollOption("egal1", "test1"), mutableListOf(
                    Vote(
                        1,
                        LocalDateTime.of(2023, 10, 21, 0, 0, 0).toString(),
                        PollVoteEnum.FALSE,
                    )
                )
            ),
            PollEntryEntity(
                PollOption("egal2", "test2"), mutableListOf(
                    Vote(
                        2,
                        LocalDateTime.of(2023, 10, 22, 0, 0, 0).toString(),
                        PollVoteEnum.TRUE,
                    )
                )
            )
        ),
        false,
        null,
    )


    context("Get and save Poll information") {

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("processPoll") {
            val generatedId = pollPort.savePoll(24, pollModel.toEntity()) { true }
            val poll = pollPort.getPoll(generatedId) { true }
            poll shouldNotBe null
            poll!!.id shouldBe generatedId
            poll.question shouldBe "This is a test"
            poll.description shouldBe ""
            poll.pollEntries shouldHaveSize 1
            with(poll.pollEntries.first()) {
                pollOption.text shouldBe "testCase"
                pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.FALSE
                    date.isNotEmpty() shouldBe true
                }
            }
        }

        test("get present poll") {
            val entity = pollPort.getPoll(mockedPresentDatabasePollId) { true }
            entity shouldNotBe null
            requireNotNull(entity)
            entity shouldBeEqualToComparingFields mockedInDatabasePollModel.toEntity()
        }

        test("get not present poll") {
            val entity = pollPort.getPoll(mockedNotPresentPollId) { true }
            entity shouldBe null
        }
    }

}
)
