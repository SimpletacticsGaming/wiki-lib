package de.simpletactics.wiki.lib.service.port;

public interface WikiRightsPort {


  String getWikiBerechtigung(String username, int wikiId);

  void deleteWikiDefaultRead(int wikiId);

  void deleteWikiAllRights(int wikiId);

  void setWikiRightsCopyOvertopic(int wikiId, int overtopicId);
}
