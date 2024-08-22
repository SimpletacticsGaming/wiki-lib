package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.model.poll.PollOption
import de.simpletactics.model.poll.PollVoteEnum
import de.simpletactics.model.poll.Vote
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollModel
import de.simpletactics.wiki.lib.adapter.persistence.mapper.toEntity
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testing")
class SetPollVotesAdapterTest(
    val pollPort: PollPort
) : FunSpec({

    var generatedId: Int? = null
    val pollModel = PollModel(
        null,
        "This is a test",
        "Test description",
        listOf(
            PollEntryEntity(
                PollOption("ddb11436-bdc8-4488-87f6-fsdfsd", "testCase"),
                mutableListOf(),
            ),
            PollEntryEntity(
                PollOption("aaa1436-bdc8-4488-87f6-fsdfsd", "testCaseTwo"),
                mutableListOf(),
            )
        ),
        false,
        null,
    )

    context("Get and save Poll information") {

        beforeEach {
            generatedId = pollPort.savePoll(24, pollModel.toEntity())
        }

        afterEach {
            if (generatedId != null) {
                pollPort.deletePoll(generatedId!!)
            }
        }

        test("set first poll votes test") {
            val pollWithId = pollPort.getPoll(generatedId!!)
            pollWithId!!.pollEntries.forEach {
                it.votes.add(Vote(1, Date.getDateAsString(), PollVoteEnum.TRUE))
            }

            pollPort.setPollVotes(1, pollWithId)

            val poll = pollPort.getPoll(generatedId!!)

            poll shouldNotBe null
            poll!!.id shouldBe generatedId
            poll.question shouldBe "This is a test"
            poll.description shouldBe "Test description"
            poll.pollEntries shouldHaveSize 2
            with(poll.pollEntries[0]) {
                pollOption.text shouldBe "testCase"
                pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
            }
            with(poll.pollEntries[1]) {
                pollOption.text shouldBe "testCaseTwo"
                pollOption.uuid shouldBe "aaa1436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
            }
        }

        test("should not override old votes which did not change test") {
            val pollWithId = pollPort.getPoll(generatedId!!)
            val date = Date.getDateAsString()
            pollWithId!!.pollEntries.first {
                it.votes.add(Vote(1, date, PollVoteEnum.TRUE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId)
            val modifiedPoll = pollPort.getPoll(modifiedPollId)
            modifiedPoll!!.pollEntries[1].votes.add(Vote(1, Date.getDateAsString(), PollVoteEnum.FALSE))

            pollPort.setPollVotes(1, modifiedPoll)

            val poll = pollPort.getPoll(modifiedPollId)

            poll shouldNotBe null
            poll!!.id shouldBe modifiedPollId
            poll.question shouldBe "This is a test"
            poll.description shouldBe "Test description"
            poll.pollEntries shouldHaveSize 2

            with(poll.pollEntries[0]) {
                pollOption.text shouldBe "testCase"
                pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.TRUE
                    date shouldBeEqual date
                }
            }
            with(poll.pollEntries[1]) {
                pollOption.text shouldBe "testCaseTwo"
                pollOption.uuid shouldBe "aaa1436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.FALSE
                    date.isNotEmpty() shouldBe true
                }
            }
        }

        test("should not override other user votes") {
            val pollWithId = pollPort.getPoll(generatedId!!)
            pollWithId!!.pollEntries.first {
                it.votes.add(Vote(3, Date.getDateAsString(), PollVoteEnum.TRUE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId)
            val modifiedPoll = pollPort.getPoll(modifiedPollId)
            modifiedPoll!!.pollEntries[0].votes.add(Vote(1, Date.getDateAsString(), PollVoteEnum.TRUE))
            modifiedPoll.pollEntries[1].votes.add(Vote(1, Date.getDateAsString(), PollVoteEnum.TRUE))

            pollPort.setPollVotes(1, modifiedPoll)

            val poll = pollPort.getPoll(modifiedPollId)

            poll shouldNotBe null
            poll!!.id shouldBe modifiedPollId
            poll.question shouldBe "This is a test"
            poll.description shouldBe "Test description"
            poll.pollEntries shouldHaveSize 2

            with(poll.pollEntries[0]) {
                pollOption.text shouldBe "testCase"
                pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 2
                with(votes[0]) {
                    userId shouldBe 3
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
                with(votes[1]) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
            }

            with(poll.pollEntries[1]) {
                pollOption.text shouldBe "testCaseTwo"
                pollOption.uuid shouldBe "aaa1436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes.first()) {
                    userId shouldBe 1
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
            }
        }

        test("should override own vote on change test") {
            val pollWithId = pollPort.getPoll(generatedId!!)
            pollWithId!!.pollEntries.first {
                it.votes.add(Vote(3, Date.getDateAsString(), PollVoteEnum.FALSE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId)
            val modifiedPoll = pollPort.getPoll(modifiedPollId)
            modifiedPoll!!.pollEntries.forEach {
                it.votes.replaceAll {
                    Vote(
                        3,
                        Date.getDateAsString(),
                        PollVoteEnum.TRUE
                    )
                }
            }
            modifiedPoll.pollEntries[1].votes.add(Vote(3, Date.getDateAsString(), PollVoteEnum.FALSE))

            pollPort.setPollVotes(3, modifiedPoll)

            val poll = pollPort.getPoll(modifiedPollId)

            poll shouldNotBe null
            poll!!.id shouldBe modifiedPollId
            poll.question shouldBe "This is a test"
            poll.description shouldBe "Test description"
            poll.pollEntries shouldHaveSize 2

            with(poll.pollEntries[0]) {
                pollOption.text shouldBe "testCase"
                pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes[0]) {
                    userId shouldBe 3
                    option shouldBe PollVoteEnum.TRUE
                    date.isNotEmpty() shouldBe true
                }
            }
            with(poll.pollEntries[1]) {
                pollOption.text shouldBe "testCaseTwo"
                pollOption.uuid shouldBe "aaa1436-bdc8-4488-87f6-fsdfsd"
                votes shouldHaveSize 1
                with(votes[0]) {
                    userId shouldBe 3
                    option shouldBe PollVoteEnum.FALSE
                    date.isNotEmpty() shouldBe true
                }
            }
        }

    }
}
)
