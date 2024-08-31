package de.simpletactics.wiki.lib.adapter.persistence

import de.simpletactics.wiki.lib.adapter.dto.poll.Date
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import de.simpletactics.wiki.lib.adapter.persistence.mapper.IdMapper
import de.simpletactics.wiki.lib.adapter.persistence.mapper.PollMapper
import de.simpletactics.wiki.lib.adapter.persistence.mapper.PollOpenBooleanMapper
import de.simpletactics.wiki.lib.model.WikiNotFoundException
import de.simpletactics.wiki.lib.util.jsonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class PollSqlAdapter(
    private val jdbc: JdbcTemplate,
) {

    fun savePoll(
        poll: PollEntity,
    ): Int {
        return jdbc.query(
            "insert into wiki_poll (question, description, end_date, ended, data) values(?, ?, ?, ?, ?::jsonb) RETURNING id",
            IdMapper(),
            poll.question,
            poll.description,
            poll.date,
            poll.ended,
            jsonObjectMapper.writeValueAsString(poll.pollEntries)
        ).first()
    }

    fun getPoll(id: Int): PollEntity? {
        return jdbc.query("select * from wiki_poll where id = ?", PollMapper(), id).first() ?: null
    }

    fun updatePoll(poll: PollEntity): PollEntity? {
        if (poll.id == null) throw WikiNotFoundException("Couldn't update poll. Poll id is null")

        getPoll(poll.id)?.let { updatingEntries(poll, it) }
        return getPoll(poll.id)
    }

    fun setPollVotes(
        userId: Int,
        pollModel: PollEntity,
    ) {
        if (pollModel.id != null && getPoll(pollModel.id) == null) {
            // TODO: throw Exception???
            log.warn("Can not set poll votes because there is no poll with id ${pollModel.id}")
            return
        }

        pollModel.pollEntries.forEach {
            val option = getUserVoteOption(it)
            val date = Date.getDateAsString()

            jdbc.execute(
                "UPDATE wiki_poll set data = (" +
                        "SELECT JSONB_AGG(" +
                        "CASE when d.value @? '$.pollOption.uuid ? (@ == \"${it.pollOption.uuid}\")' " +
                        "THEN " +
                        "CASE when d.value @? '$.votes ?(@.userId == $userId)' " +
                        "THEN jsonb_set(d.value, '{votes}', " +
                        "(SELECT JSONB_AGG( " +
                        "CASE when dd.value @? '$ ?(@.userId == $userId)' AND NOT dd.value -> 'option' @> '\"$option\"'::jsonb " +
                        "THEN jsonb_set(jsonb_set(dd.value," +
                        "'{option}', '\"$option\"')" +
                        ", '{date}', '\"$date\"') " +
                        "ELSE dd.value " +
                        "END) " +
                        "FROM jsonb_array_elements(jsonb_path_query_array(d.value, '$.votes[*]')) dd)" +
                        " ) " +
                        "ELSE jsonb_insert(d.value, '{votes, -1}', '{" +
                        "\"userId\": $userId," +
                        "\"date\": \"$date\"," +
                        "\"option\": \"$option\"" +
                        "}', true) " +
                        "END " +
                        "ELSE d.value " +
                        "END) " +
                        "FROM jsonb_array_elements(data) d) " +
                        "where id = ${pollModel.id}",
            )
        }
    }

    fun deletePoll(id: Int) {
        jdbc.update("DELETE FROM wiki_poll WHERE id = ?", id)
    }

    fun endPoll(id: Int) {
        val date = Date.getSqlDateFrom(Date.getDate())
        jdbc.update("UPDATE wiki_poll SET date = '?', ended = 'true' WHERE id = ?", date, id)
    }

    fun isPollOpen(id: Int): Boolean {
        return !jdbc.query("SELECT ended FROM wiki_poll WHERE id = ?", PollOpenBooleanMapper(), id)
            .first()
    }

    fun closeOpenPolls(): Int {
        return jdbc.update("UPDATE wiki_poll SET ended = true WHERE end_date < CURRENT_DATE AND ended = false")
    }

    private fun getUserVoteOption(pollEntryEntity: PollEntryEntity) =
        pollEntryEntity.votes[0].option

    /*
     * jsonb_array_elements() teilt das Array im jsonb auf und gibt die Einzelteile aus.
     * jsonb_agg() nutzt fügt das aufgeteilte Array wieder zusammen
     * Über CASE wird entschieden, ob die Einzelteile des Array so bleiben oder
     * vor dem Zusammensetzen mit jsonb_set() geändert werden.
     */
    private fun updatingEntries(
        updatedPoll: PollEntity,
        oldPoll: PollEntity,
    ) {
        jdbc.update(
            "UPDATE wiki_poll SET question = ?, description = ?, end_date = ?, ended = ? WHERE id = ?;",
            updatedPoll.question,
            updatedPoll.description,
            updatedPoll.date,
            updatedPoll.ended,
            updatedPoll.id,
        )

        updatedPoll.pollEntries.forEach { updatedPollEntry ->
            val oldPollEntry =
                oldPoll.pollEntries.find { it.pollOption.uuid == updatedPollEntry.pollOption.uuid }

            if (oldPollEntry == null) {
                jdbc.update(
                    "UPDATE wiki_poll SET data = jsonb_insert(" +
                            "data, '{-1}', '${jsonObjectMapper.writeValueAsString(updatedPollEntry)}', true) " +
                            "WHERE id = ${updatedPoll.id};",
                )
            } else if (updatedPollEntry.pollOption.text != oldPollEntry.pollOption.text) {
                jdbc.update(
                    "UPDATE wiki_poll set data = (" +
                            "SELECT JSONB_AGG(" +
                            "CASE when (d.value->>'pollOption')::jsonb->>'uuid' = '${updatedPollEntry.pollOption.uuid}' " +
                            "THEN jsonb_set(d.value, '{pollOption,text}', '\"${updatedPollEntry.pollOption.text}\"') " +
                            "ELSE d.value " +
                            "END) " +
                            "FROM jsonb_array_elements(data) d) " +
                            "where id = ${updatedPoll.id};",
                )
            }
        }

        oldPoll.pollEntries.forEach { oldPollEntry ->
            val oldEntry =
                updatedPoll.pollEntries.find { it.pollOption.uuid == oldPollEntry.pollOption.uuid }

            if (oldEntry == null) {
                jdbc.update(
                    "update wiki_poll set data = (" +
                            "select jsonb_agg(d.jsonb_path_query) " +
                            "from (select jsonb_path_query(data, " +
                            "'$[*] ? (@.pollOption.uuid != \"${oldPollEntry.pollOption.uuid}\")')) d) " +
                            "where id = ${oldPoll.id};",
                )
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PollSqlAdapter::class.java)
    }
}
