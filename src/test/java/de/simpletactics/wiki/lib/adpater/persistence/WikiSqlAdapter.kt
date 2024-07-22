package de.simpletactics.wiki.lib.adpater.persistence

import de.simpletactics.wiki.lib.adapter.persistence.WikiSqlAdapter
import de.simpletactics.wiki.lib.model.WikiType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testing")
class WikiSqlAdapter {

    @Autowired
    private lateinit var wikiSqlAdapter: WikiSqlAdapter

    @Test
    fun getWikiTypeTest() {
        val wikiType = wikiSqlAdapter.getWikiType(11)
        assertThat(wikiType).isEqualTo(WikiType.TOPIC)
    }

    @Test
    fun getNoWikiTypeTest() {
        val wikiType = wikiSqlAdapter.getWikiType(999)
        assertThat(wikiType).isNull()
    }

}
