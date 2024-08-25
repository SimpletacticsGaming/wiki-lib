package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.persistence.PollSqlAdapter
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.services.port.PollPort
import de.simpletactics.wiki.lib.util.checkAccess
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WikiPollAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
    private val pollSqlAdapter: PollSqlAdapter,
) : PollPort {

    @Transactional
    override fun savePoll(topicId: Int, poll: PollEntity, hasAccess: () -> Boolean): Int {
        hasAccess.checkAccess("Access denied for saving poll for topicId $topicId")
        if (wikiSqlAdapter.getWikiType(topicId) != null) {
            val topic = wikiSqlAdapter.getTopic(topicId)
            if (topic != null) {
                val id = pollSqlAdapter.savePoll(poll)
                wikiSqlAdapter.updateTopic(topic.copy(childIds = topic.childIds + id))
                return id
            }
        }
        throw IllegalArgumentException("No topic found with id $topicId")
    }

    override fun getPoll(id: Int, hasAccess: () -> Boolean): PollEntity? {
        hasAccess.checkAccess("Access denied for getting poll with id $id")
        return if (wikiSqlAdapter.getWikiType(id) != null) {
            pollSqlAdapter.getPoll(id)
        } else null
    }

    @Transactional
    override fun updatePoll(poll: PollEntity, hasAccess: () -> Boolean): PollEntity? {
        hasAccess.checkAccess("Access denied for updating poll with id ${poll.id}")
        return pollSqlAdapter.updatePoll(poll)
    }

    @Transactional
    override fun setPollVotes(userId: Int, pollModel: PollEntity, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for submitting votes for user with user id $userId and poll with pollId ${pollModel.id}")
        pollSqlAdapter.setPollVotes(userId, pollModel)
    }

    @Transactional
    override fun deletePoll(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting poll with id $id")
        pollSqlAdapter.deletePoll(id)
    }

    @Transactional
    override fun endPoll(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for ending poll with id $id")
        pollSqlAdapter.endPoll(id)
    }

    override fun isPollOpen(id: Int, hasAccess: () -> Boolean): Boolean {
        hasAccess.checkAccess("Access denied for checking if poll with id $id is open")
        return pollSqlAdapter.isPollOpen(id)
    }

    // TODO: Rename to closeExpiredOpenPolls
    @Transactional
    override fun closeOpenPolls(
        hasAccess: () -> Boolean,
    ): Int {
        hasAccess.checkAccess("Access denied for closing expired polls")
        return pollSqlAdapter.closeOpenPolls()
    }

}