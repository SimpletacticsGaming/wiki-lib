package de.simpletactics.wiki.lib.services.port

import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity

interface PollPort {
    fun savePoll(
        topicId: Int,
        poll: PollEntity,
        hasAccess: () -> Boolean,
    ): Int

    fun getPoll(
        id: Int,
        hasAccess: () -> Boolean,
    ): PollEntity?

    fun updatePoll(
        poll: PollEntity,
        hasAccess: () -> Boolean,
    ): PollEntity?

    fun setPollVotes(
        userId: Int,
        pollModel: PollEntity,
        hasAccess: () -> Boolean,
    )

    fun deletePoll(
        id: Int,
        hasAccess: () -> Boolean,
    )

    fun endPoll(
        id: Int,
        hasAccess: () -> Boolean,
    )

    fun isPollOpen(
        id: Int,
        hasAccess: () -> Boolean,
    ): Boolean

    fun closeExpiredOpenPolls(
        hasAccess: () -> Boolean,
    ): Int

    fun reopenPoll(
        id: Int,
        date: String?,
        hasAccess: () -> Boolean,
    )
}
