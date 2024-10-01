package de.simpletactics.wiki.lib.adapter.persistence.poll

import de.simpletactics.model.poll.PollVoteEnum
import de.simpletactics.model.poll.Vote
import de.simpletactics.wiki.lib.adapter.FunSpecIT
import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.services.port.PollPort
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.JdbcTemplate

class SetPollVotesAdapterTest(
    private val pollPort: PollPort,
    jdbcTemplate: JdbcTemplate,
) : FunSpecIT(jdbcTemplate, {

    context("Get and save Poll information") {
        val id = 2

        beforeTest {
            executeSql("wiki_poll.sql")
        }

        test("set first poll votes") {
            val pollWithId = pollPort.getPoll(id) { true }
            pollWithId!!.pollEntries.forEach {
                it.votes.add(Vote(1, Date.getDateAsString(), PollVoteEnum.TRUE))
            }

            pollPort.setPollVotes(1, pollWithId) { true }

            val poll = pollPort.getPoll(id) { true }

            requireNotNull(poll)
            with(poll) {
                id shouldBe id
                question shouldBe "This is a test"
                description shouldBe "Test description"
                pollEntries shouldHaveSize 2
                with(pollEntries[0]) {
                    pollOption.text shouldBe "testCase"
                    pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                    votes shouldHaveSize 1
                    with(votes.first()) {
                        userId shouldBe 1
                        option shouldBe PollVoteEnum.TRUE
                        date.isNotEmpty() shouldBe true
                    }
                }
                with(pollEntries[1]) {
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
        }

        test("should not override old votes which did not change") {
            val pollWithId = pollPort.getPoll(id) { true }
            val date = Date.getDateAsString()
            pollWithId!!.pollEntries.first {
                it.votes.add(Vote(1, date, PollVoteEnum.TRUE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId) { true }
            val modifiedPoll = pollPort.getPoll(modifiedPollId) { true }
            modifiedPoll!!.pollEntries[1].votes.add(
                Vote(
                    1,
                    Date.getDateAsString(),
                    PollVoteEnum.FALSE
                )
            )

            pollPort.setPollVotes(1, modifiedPoll) { true }

            val poll = pollPort.getPoll(modifiedPollId) { true }

            requireNotNull(poll)
            with(poll) {
                this.id shouldBe modifiedPollId
                question shouldBe "This is a test"
                description shouldBe "Test description"
                pollEntries shouldHaveSize 2

                with(pollEntries[0]) {
                    pollOption.text shouldBe "testCase"
                    pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                    votes shouldHaveSize 1
                    with(votes.first()) {
                        userId shouldBe 1
                        option shouldBe PollVoteEnum.TRUE
                        this.date shouldBeEqual date
                    }
                }
                with(pollEntries[1]) {
                    pollOption.text shouldBe "testCaseTwo"
                    pollOption.uuid shouldBe "aaa1436-bdc8-4488-87f6-fsdfsd"
                    votes shouldHaveSize 1
                    with(votes.first()) {
                        userId shouldBe 1
                        option shouldBe PollVoteEnum.FALSE
                        this.date.isNotEmpty() shouldBe true
                    }
                }
            }
        }

        test("should not override other user votes") {
            val pollWithId = pollPort.getPoll(id) { true }

            requireNotNull(pollWithId)
            pollWithId.pollEntries.first {
                it.votes.add(Vote(3, Date.getDateAsString(), PollVoteEnum.TRUE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId) { true }
            val modifiedPoll = pollPort.getPoll(modifiedPollId) { true }
            requireNotNull(modifiedPoll)
            modifiedPoll.pollEntries[0].votes.add(
                Vote(
                    1,
                    Date.getDateAsString(),
                    PollVoteEnum.TRUE
                )
            )
            modifiedPoll.pollEntries[1].votes.add(
                Vote(
                    1,
                    Date.getDateAsString(),
                    PollVoteEnum.TRUE
                )
            )

            pollPort.setPollVotes(1, modifiedPoll) { true }

            val poll = pollPort.getPoll(modifiedPollId) { true }

            requireNotNull(poll)
            with(poll) {
                this.id shouldBe modifiedPollId
                question shouldBe "This is a test"
                description shouldBe "Test description"
                pollEntries shouldHaveSize 2

                with(pollEntries[0]) {
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

                with(pollEntries[1]) {
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
        }

        test("should override own vote on change test") {
            val pollWithId = pollPort.getPoll(id) { true }

            requireNotNull(pollWithId)
            pollWithId.pollEntries.first {
                it.votes.add(Vote(3, Date.getDateAsString(), PollVoteEnum.FALSE))
            }

            val modifiedPollId = pollPort.savePoll(24, pollWithId) { true }
            val modifiedPoll = pollPort.getPoll(modifiedPollId) { true }

            requireNotNull(modifiedPoll)
            modifiedPoll.pollEntries.forEach {
                it.votes.replaceAll {
                    Vote(
                        3,
                        Date.getDateAsString(),
                        PollVoteEnum.TRUE
                    )
                }
            }
            modifiedPoll.pollEntries[1].votes.add(
                Vote(
                    3,
                    Date.getDateAsString(),
                    PollVoteEnum.FALSE
                )
            )

            pollPort.setPollVotes(3, modifiedPoll) { true }

            val poll = pollPort.getPoll(modifiedPollId) { true }

            requireNotNull(poll)
            with(poll) {
                this.id shouldBe modifiedPollId
                question shouldBe "This is a test"
                description shouldBe "Test description"
                pollEntries shouldHaveSize 2

                with(pollEntries[0]) {
                    pollOption.text shouldBe "testCase"
                    pollOption.uuid shouldBe "ddb11436-bdc8-4488-87f6-fsdfsd"
                    votes shouldHaveSize 1
                    with(votes[0]) {
                        userId shouldBe 3
                        option shouldBe PollVoteEnum.TRUE
                        date.isNotEmpty() shouldBe true
                    }
                }
                with(pollEntries[1]) {
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
})
