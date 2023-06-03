package de.simpletactics.wiki.lib.service.port;

import java.util.List;
import java.util.Map;

public interface UserPort {

  List<Map<String, Object>> getUsers();

  String getUserWikiRights(String username, int wikiId);

  List<Map<String, Object>> getUserRoles(String username);

  List<Map<String, Object>> getWikiUsersWithRights(int wikiId);

  void updateUserByUserId(
      String id,
      String username,
      boolean setPW,
      boolean active,
      String firstname,
      String lastname,
      String email);

  List<Map<String, Object>> isSetPW(String username);

  List<Map<String, Object>> getUserById(String userId);

  boolean isUsernameFree(String username);
}
