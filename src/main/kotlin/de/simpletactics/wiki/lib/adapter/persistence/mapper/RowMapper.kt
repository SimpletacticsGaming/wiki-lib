package de.simpletactics.wiki.lib.adapter.persistence.mapper

import com.fasterxml.jackson.module.kotlin.readValue
import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntity
import de.simpletactics.wiki.lib.adapter.dto.poll.PollEntryEntity
import de.simpletactics.wiki.lib.util.jsonObjectMapper
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class TopicRowMapper : RowMapper<TopicEntity> {

    override fun mapRow(resultSet: ResultSet, rowNum: Int): TopicEntity = with(resultSet) {
        return TopicEntity(
            getInt("id"),
            getString("topic"),
            (getArray("child_id").array as Array<Int>).toList()
        )
    }

}

class EntryMapper : RowMapper<EntryEntity> {

    override fun mapRow(resultSet: ResultSet, rowNum: Int): EntryEntity = with(resultSet) {
        return EntryEntity(
            getInt("id"),
            getString("headline"),
            getString("body")
        )
    }

}

class IdMapper : RowMapper<Int> {
    override fun mapRow(resultSet: ResultSet, rowNum: Int): Int = resultSet.getInt("id")
}

class PollMapper : RowMapper<PollEntity> {
    override fun mapRow(resultSet: ResultSet, rowNum: Int): PollEntity = with(resultSet) {
        return PollEntity(
            getInt("id"),
            getString("question"),
            getString("description"),
            parseJsonToPollEntity(getString("data") ?: "[]"),
            getBoolean("ended"),
            getDate("end_date"),
        )
    }

    private fun parseJsonToPollEntity(json: String): List<PollEntryEntity> {
        return jsonObjectMapper.readValue<List<PollEntryEntity>>(json)
    }
}

class PollOpenBooleanMapper : RowMapper<Boolean> {
    override fun mapRow(resultSet: ResultSet, rowNum: Int): Boolean = resultSet.getBoolean("ended")
}