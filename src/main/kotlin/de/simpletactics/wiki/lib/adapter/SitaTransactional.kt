package de.simpletactics.wiki.lib.adapter

import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException


@Transactional(rollbackFor = [SQLException::class])
annotation class SitaTransactional
