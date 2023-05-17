package de.simpletactics.wiki.lib.service.port;

public interface WikiRightsPort {

  int getWikiDefaultRead(int wikiId);

  String getWikiBerechtigung(String username, int wikiId);

  void addWikiUserRight(int wikiId, String username, String wikiRight, boolean recursive);

  void deleteWikiUserRight(int wikiId, String username);

  void addWikiRoleRight(int wikiId, int role, String wikiRight, boolean recursive);

  void setWikiDefaultRead(int wikiId, int roleId);

  void deleteWikiDefaultRead(int wikiId);

  void deleteWikiAllRights(int wikiId);

  void setWikiRightsCopyOvertopic(int wikiId, int overtopicId);
}
