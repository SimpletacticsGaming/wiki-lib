package de.simpletactics.wiki.lib.adapter

import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.persistence.PollSqlAdapter
import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import de.simpletactics.wiki.lib.services.port.PollPort
import de.simpletactics.wiki.lib.util.checkAccess
import de.simpletactics.wiki.lib.util.verify
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class WikiPollAdapter(
    private val wikiSqlAdapter: WikiSqlAdapter,
    private val pollSqlAdapter: PollSqlAdapter,
) : PollPort {

    @SitaTransactional
    override fun savePoll(topicId: Int, poll: PollEntity, hasAccess: () -> Boolean): Int {
        hasAccess.checkAccess("Access denied for saving poll for topicId $topicId")

        val wikiType = wikiSqlAdapter.getWikiType(topicId)
        val topic = wikiSqlAdapter.getTopic(topicId)

        verify(wikiType, WikiType.TOPIC, topic) {
            "No topic found with id $topicId"
        }

        val id = pollSqlAdapter.savePoll(poll)
        wikiSqlAdapter.updateTopic(topic.copy(childIds = topic.childIds + id))
        return id
    }

    override fun getPoll(id: Int, hasAccess: () -> Boolean): PollEntity? {
        hasAccess.checkAccess("Access denied for getting poll with id $id")
        return wikiSqlAdapter.getWikiType(id)?.let { pollSqlAdapter.getPoll(id) }
    }

    @SitaTransactional
    override fun updatePoll(poll: PollEntity, hasAccess: () -> Boolean): PollEntity? {
        hasAccess.checkAccess("Access denied for updating poll with id ${poll.id}")
        return pollSqlAdapter.updatePoll(poll)
    }

    @SitaTransactional
    override fun setPollVotes(userId: Int, pollModel: PollEntity, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for submitting votes for user with user id $userId and poll with pollId ${pollModel.id}")
        pollSqlAdapter.setPollVotes(userId, pollModel)
    }

    @SitaTransactional
    override fun deletePoll(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for deleting poll with id $id")
        val wikiType = wikiSqlAdapter.getWikiType(id)
        val topicEntity = wikiSqlAdapter.getTopicForChild(id)
        verify(wikiType, WikiType.POLL, topicEntity) { "No parent found for id $id" }
        wikiSqlAdapter.updateTopic(
            topicEntity.copy(childIds = topicEntity.childIds.toMutableList().apply {
                remove(id)
            })
        )
        pollSqlAdapter.deletePoll(id)
    }

    @SitaTransactional
    override fun endPoll(id: Int, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for ending poll with id $id")
        pollSqlAdapter.endPoll(id)
    }

    override fun isPollOpen(id: Int, hasAccess: () -> Boolean): Boolean {
        hasAccess.checkAccess("Access denied for checking if poll with id $id is open")
        return pollSqlAdapter.isPollOpen(id)
    }

    @SitaTransactional
    override fun closeExpiredOpenPolls(
        hasAccess: () -> Boolean,
    ): Int {
        hasAccess.checkAccess("Access denied for closing expired polls")
        return pollSqlAdapter.closeOpenPolls()
    }

    @SitaTransactional
    override fun reopenPoll(id: Int, date: ZonedDateTime?, hasAccess: () -> Boolean) {
        hasAccess.checkAccess("Access denied for reopening poll")
        pollSqlAdapter.reopenPoll(id, date)
    }

}