package de.simpletactics.wiki.lib.adapter.persistence;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserAdapter implements UserPort {

  private final JdbcTemplate jdbc;
  @Autowired
  private WikiEntryAdapter wikiEntryAdapter;

  public UserAdapter(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  //gibt alle Benutzer zurück
  @Override
  public List<Map<String, Object>> getUsers() {
    return jdbc.queryForList("SELECT user_name AS username FROM users");
  }

  //gibt die Berechtigung eines Users auf einen Wiki-Eintrag zurück
  @Override
  public String getUserWikiRights(String username, int wikiId) {
    List<Map<String, Object>> lBerechtigung = jdbc.queryForList(
        "SELECT w.berechtigung FROM wiki_user_berechtigung AS w INNER JOIN users AS u ON w.user_id = u.user_id WHERE w.wiki_id = "
            + wikiId + " && u.user_name = '" + username + "';");
    String berechtigung = "";
    if (!lBerechtigung.isEmpty()) {
      berechtigung = lBerechtigung.get(0).get("berechtigung").toString();
    }
    return berechtigung;
  }

  @Override
  public List<Map<String, Object>> getUserRoles(String username) {
    return jdbc.queryForList(
        "SELECT r.role_id as id, r.role as role FROM roles AS r "
            + "INNER JOIN user_role AS ur ON r.role_id = ur.role_id "
            + "INNER JOIN users AS u ON ur.user_id = u.user_id "
            + "WHERE u.user_name = '" + username + "';");
  }

  //Gibt alle Wiki Benutzer zurück die eine Berechtigung auf die übergebene WikiID haben
  @Override
  public List<Map<String, Object>> getWikiUsersWithRights(int wikiId) {
    return jdbc.queryForList(
        "SELECT u.user_name AS username, wub.berechtigung AS wikiRight FROM wiki_user_berechtigung AS wub INNER JOIN users AS u ON wub.user_id = u.user_id WHERE wub.wiki_id ="
            + wikiId + ";");
  }

  @Override
  public void updateUserByUserId(String id, String username, boolean setPW, boolean active,
      String firstname, String lastname, String email) {
    jdbc.execute(
        "UPDATE users SET user_name = '" + username + "', first_name = '" + firstname
            + "', last_name = '" + lastname + "', e_mail = '" + email + "', pw_setzen = " + setPW
            + ", active = " + active
            + " WHERE user_id = " + id
            + ";");
  }

  @Override
  public List<Map<String, Object>> isSetPW(String username) {
    return jdbc.queryForList("SELECT pw_setzen AS setPW "
        + "FROM users "
        + "WHERE user_name = '" + username + "';");
  }

  @Override
  public List<Map<String, Object>> getUserById(String userId) {
    return jdbc.queryForList("SELECT * FROM users WHERE user_id = '" + userId + "';");
  }

  @Override
  public boolean isUsernameFree(String username) {
    return jdbc.queryForList("SELECT * FROM users WHERE user_name = '" + username + "';").isEmpty();
  }
}
