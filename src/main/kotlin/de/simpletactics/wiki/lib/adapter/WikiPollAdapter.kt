package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.persistence.PollSqlAdapter
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.services.port.PollPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WikiPollAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
    private val pollSqlAdapter: PollSqlAdapter,
) : PollPort {

    @Transactional
    override fun savePoll(topicId: Int, poll: PollEntity, hasAccess: () -> Boolean): Int {
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
        return if (wikiSqlAdapter.getWikiType(id) != null) {
            pollSqlAdapter.getPoll(id)
        } else null
    }

    @Transactional
    override fun updatePoll(poll: PollEntity, hasAccess: () -> Boolean): PollEntity? {
        return pollSqlAdapter.updatePoll(poll)
    }

    @Transactional
    override fun setPollVotes(userId: Int, pollModel: PollEntity, hasAccess: () -> Boolean) {
        pollSqlAdapter.setPollVotes(userId, pollModel)
    }

    @Transactional
    override fun deletePoll(id: Int, hasAccess: () -> Boolean) {
        pollSqlAdapter.deletePoll(id)
    }

    @Transactional
    override fun endPoll(id: Int, hasAccess: () -> Boolean) {
        pollSqlAdapter.endPoll(id)
    }

    override fun isPollOpen(id: Int, hasAccess: () -> Boolean): Boolean {
        return pollSqlAdapter.isPollOpen(id)
    }

    @Transactional
    override fun closeOpenPolls(
        hasAccess: () -> Boolean,
    ): Int {
        return pollSqlAdapter.closeOpenPolls()
    }
}