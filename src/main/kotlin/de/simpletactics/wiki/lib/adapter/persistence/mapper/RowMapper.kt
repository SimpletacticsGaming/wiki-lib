package de.simpletactics.wiki.lib.adapter.persistence.mapper

import de.simpletactics.wiki.lib.adapter.dto.EntryEntity
import de.simpletactics.wiki.lib.adapter.dto.TopicEntity
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