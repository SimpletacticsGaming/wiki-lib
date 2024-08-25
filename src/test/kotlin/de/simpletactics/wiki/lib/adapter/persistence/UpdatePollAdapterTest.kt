package de.simpletactics.wiki.lib.adapter.persistence

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
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.*


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testing")
class UpdatePollAdapterTest(
    val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    var generatedId: Int? = null
    val pollModel = PollModel(
        null,
        "Old test entity",
        "old description",
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

    context("Update polls") {

        beforeEach {
            generatedId = pollPort.savePoll(24, pollModel.toEntity()) { true }
        }

        afterEach {
            if (generatedId != null) {
                pollPort.deletePoll(generatedId!!) { true }
            }
        }

        test("test update question and description") {
            val updatedQuestion = "Updated question"
            val updatedDescription = "Updated description"
            val updatedPoll = pollModel.copy(generatedId, updatedQuestion, updatedDescription)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update date") {
            val sqlDate = Date.getDate()
            val updatedPoll = pollModel.copy(generatedId, date = sqlDate)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update date set null") {
            val updatedPoll = pollModel.copy(generatedId, date = null)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with insert") {
            val pollEntries = pollModel.pollEntries.toMutableList()
            pollEntries.add(
                PollEntryEntity(
                    PollOption(UUID.randomUUID().toString(), "New Option"),
                    mutableListOf()
                )
            )
            val updatedPoll = pollModel.copy(generatedId, pollEntries = pollEntries)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with update") {
            val pollEntries = pollModel.pollEntries.toMutableList()
            pollEntries.replaceAll {
                if (it.pollOption.uuid == "ddb11436-bdc8-4488-87f6-fsdfsd") {
                    PollEntryEntity(PollOption(it.pollOption.uuid, "Updated entry"), it.votes)
                } else {
                    it
                }
            }
            val updatedPoll = pollModel.copy(generatedId, pollEntries = pollEntries)
            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with delete") {
            val pollEntries = pollModel.pollEntries.toMutableList()
            pollEntries.removeFirst()
            val updatedPoll = pollModel.copy(generatedId, pollEntries = pollEntries)
            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll.pollEntries shouldHaveSize 0
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }
    }

}
)
