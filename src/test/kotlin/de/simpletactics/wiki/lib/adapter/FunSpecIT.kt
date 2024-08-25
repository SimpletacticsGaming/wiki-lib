package de.simpletactics.wiki.lib.adapter

import io.kotest.core.spec.style.FunSpec
import org.springframework.jdbc.core.JdbcTemplate

open class FunSpecIT(
    private val jdbcTemplate: JdbcTemplate,
    body: FunSpecIT.() -> Unit = {}
) : FunSpec() {

    fun executeSql(path: String) {
        jdbcTemplate.execute(this::class.java.getResource("/scripts/$path")!!.readText())
    }

    init {
        beforeSpec {
            executeSql("init.sql")
        }

        afterTest {
            executeSql("clean_up.sql")
        }

        body()
    }
}
