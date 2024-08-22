package de.simpletactics.wiki.lib.adapter.persistence.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class TopicRowMapper : RowMapper<TopicEntity> {

    override fun mapRow(rs: ResultSet, rowNum: Int): TopicEntity? {
        val id = rs.getInt("id")
        val topic = rs.getString("topic")
        val childIds = rs.getArray("child_id")
        val ids = childIds.array as Array<Int>
        return TopicEntity(id, topic, ids.toList())
    }

}

class EntryMapper : RowMapper<EntryEntity> {

    override fun mapRow(rs: ResultSet, rowNum: Int): EntryEntity? {
        val id = rs.getInt("id")
        val headline = rs.getString("headline")
        val body = rs.getString("body")
        return EntryEntity(id, headline, body)
    }

}

class IdMapper : RowMapper<Int> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Int? {
        return rs.getInt("id")
    }
}

class PollMapper : RowMapper<PollEntity> {
    override fun mapRow(rs: ResultSet, rowNum: Int): PollEntity? {
        val id = rs.getInt("id")
        val question = rs.getString("question")
        val description = rs.getString("description")
        val entries = parseJsonToPollEntity(rs.getString("data") ?: "[]")
        val ended = rs.getBoolean("ended")
        val date = rs.getDate("end_date")
        return PollEntity(id, question, description, entries, ended, date)
    }

    private fun parseJsonToPollEntity(json: String): List<PollEntryEntity> {
        val mapper = ObjectMapper()
        return mapper.readValue(
            json,
            mapper.typeFactory.constructCollectionType(
                List::class.java,
                PollEntryEntity::class.java,
            ),
        )
    }
}

class PollOpenBooleanMapper : RowMapper<Boolean> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Boolean? {
        return rs.getBoolean("ended")
    }
}