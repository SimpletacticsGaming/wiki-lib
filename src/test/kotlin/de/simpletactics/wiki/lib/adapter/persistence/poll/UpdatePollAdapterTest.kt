package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.model.poll.PollOption
import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import de.simpletactics.wiki.lib.adapter.persistence.mapper.toEntity
import de.simpletactics.wiki.lib.adapter.persistence.mapper.toModel
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*

class UpdatePollAdapterTest(
    private val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    val generatedId = 3

    fun getPollFromDb(
    ) = pollPort.getPoll(generatedId) { true }!!.toModel()


    context("Update polls") {

        beforeTest {
            executeSql("wiki_poll.sql")

        }

        test("test update question and description") {
            val updatedQuestion = "Updated question"
            val updatedDescription = "Updated description"
            val updatedPoll = getPollFromDb().copy(generatedId, updatedQuestion, updatedDescription)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update date") {
            val sqlDate = Date.getDate()
            val updatedPoll = getPollFromDb().copy(generatedId, date = sqlDate)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update date set null") {
            val updatedPoll = getPollFromDb().copy(generatedId, date = null)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with insert") {
            val pollEntries = getPollFromDb().pollEntries.toMutableList()
            pollEntries.add(
                PollEntryEntity(
                    PollOption(UUID.randomUUID().toString(), "New Option"),
                    mutableListOf()
                )
            )
            val updatedPoll = getPollFromDb().copy(generatedId, pollEntries = pollEntries)

            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with update") {
            val pollEntries = getPollFromDb().pollEntries.toMutableList()
            pollEntries.replaceAll {
                if (it.pollOption.uuid == "ddb11436-bdc8-4488-87f6-fsdfsd") {
                    PollEntryEntity(PollOption(it.pollOption.uuid, "Updated entry"), it.votes)
                } else {
                    it
                }
            }
            val updatedPoll = getPollFromDb().copy(generatedId, pollEntries = pollEntries)
            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }

        test("test update poll entries with delete") {
            val pollEntries = getPollFromDb().pollEntries.toMutableList()
            pollEntries.removeFirst()
            val updatedPoll = getPollFromDb().copy(generatedId, pollEntries = pollEntries)
            val fetchedPoll = pollPort.updatePoll(updatedPoll.toEntity()) { true }

            fetchedPoll shouldNotBe null
            requireNotNull(fetchedPoll)
            fetchedPoll.pollEntries shouldHaveSize 0
            fetchedPoll shouldBeEqualToComparingFields updatedPoll.toEntity()
        }
    }
}
)