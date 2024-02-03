package de.simpletactics.wiki.lib.services.port

interface WikiRightsPort {

    fun getWikiBerechtigung(username: String, wikiId: Int): String?

    fun deleteWikiDefaultRead(wikiId: Int)

    fun deleteWikiAllRights(wikiId: Int)

    fun setWikiRightsCopyOvertopic(wikiId: Int, overtopicId: Int)

    fun getWikiDefaultRead(wikiId: Int): Int

    fun addWikiUserRight(wikiId: Int, username: String, wikiRight: String, recursive: Boolean)

    fun deleteWikiUserRight(wikiId: Int, username: String)

    fun addWikiRoleRight(wikiId: Int, role: Int, wikiRight: String, recursive: Boolean)

    fun setWikiDefaultRead(wikiId: Int, roleId: Int)

}