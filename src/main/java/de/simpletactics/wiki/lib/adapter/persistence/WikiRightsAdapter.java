package de.simpletactics.wiki.lib.adapter.persistence;

import de.simpletactics.wiki.lib.service.port.WikiRightsPort;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WikiRightsAdapter implements WikiRightsPort {

  private final JdbcTemplate jdbc;
  @Autowired
  private UserAdapter userAdapter;
  @Autowired
  private RoleAdapter roleAdapter;
  @Autowired
  private WikiEntryAdapter wikiJdbcService;

  public WikiRightsAdapter(JdbcTemplate jdbcTemplate) {
    this.jdbc = jdbcTemplate;
    new UserAdapter(jdbcTemplate);
    new RoleAdapter(jdbcTemplate);
  }
  //Gibt die default Read Rolle für einen Wiki-Eintrag aus.

  @Override
  public int getWikiDefaultRead(int wikiId) {
    int role = 3;
    List<Map<String, Object>> lRole = jdbc.queryForList(
        "SELECT role_id FROM wiki_default_read WHERE wiki_id = " + wikiId + "");
    if (!lRole.isEmpty()) {
      role = Integer.parseInt(lRole.get(0).get("role_id").toString());
    }
    return role;
  }

  //Gibt die höchste Berechtigung eines Users auf eine wikiId zurück
  @Override
  public String getWikiBerechtigung(String username, int wikiId) {
    boolean read = false;
    boolean edit = false;
    boolean create = false;
    String right;
    List<Map<String, Object>> roles = userAdapter.getUserRoles(username);
    int defaultRead = getWikiDefaultRead(wikiId);
    for (Map<String, Object> role : roles) {
      if (Integer.parseInt(role.get("id").toString()) == 1) {
        return "admin";
      } else if (Integer.parseInt(role.get("id").toString()) == 2) {
        return "mod";
      } else if (Integer.parseInt(role.get("id").toString()) == defaultRead) {
        read = true;
      }
    }
    for (Map<String, Object> role : roles) {
      right = roleAdapter.getRoleWikiRights(Integer.parseInt(role.get("id").toString()), wikiId);
      switch (right) {
        case "write":
          return "write";
        case "create":
          create = true;
          break;
        case "edit":
          edit = true;
          break;
        case "read":
          read = true;
          break;
      }
    }
    right = userAdapter.getUserWikiRights(username, wikiId);
    switch (right) {
      case "write":
        return "write";
      case "create":
        create = true;
        break;
      case "edit":
        edit = true;
        break;
      case "read":
        read = true;
        break;
    }
    if (create) {
      return "create";
    } else if (edit) {
      return "edit";
    } else if (read) {
      return "read";
    } else if (getWikiBerechtigungRecursive(username, wikiId, roles).equals("read")) {
      return "read";
    }
    return "none";
  }

  private String getWikiBerechtigungRecursive(String username, int wikiId,
      List<Map<String, Object>> roles) {
    List<Map<String, Object>> contents = wikiJdbcService.getContents(wikiId);

    for (Map<String, Object> content : contents) {
      if (!userAdapter.getUserWikiRights(username, Integer.parseInt(content.get("id").toString()))
          .equals("")) {
        return "read";
      }
      for (Map<String, Object> role : roles) {
        if (!roleAdapter.getRoleWikiRights(Integer.parseInt(role.get("id").toString()),
            Integer.parseInt(content.get("id").toString())).equals("")) {
          return "read";
        }
      }
    }
    List<Map<String, Object>> topics = wikiJdbcService.getTopics(wikiId);
    for (Map<String, Object> topic : topics) {
      if (!userAdapter.getUserWikiRights(username, Integer.parseInt(topic.get("id").toString()))
          .equals("")) {
        return "read";
      }
      for (Map<String, Object> role : roles) {
        if (!roleAdapter.getRoleWikiRights(Integer.parseInt(role.get("id").toString()),
            Integer.parseInt(topic.get("id").toString())).equals("")) {
          return "read";
        }
      }
    }
    for (Map<String, Object> topic : topics) {
      if (!getWikiBerechtigungRecursive(username, Integer.parseInt(topic.get("id").toString()),
          roles).equals("none")) {
        return "read";
      }
    }
    return "none";
  }

  //Einem Benutzer eine Berechtigung auf eine WikiID hinzufügen oder aktuallisieren
  @Override
  public void addWikiUserRight(int wikiId, String username, String wikiRight, boolean recursive) {
    List<Map<String, Object>> check = jdbc.queryForList(
        "SELECT * FROM wiki_user_berechtigung AS wub INNER JOIN users as u ON  wub.user_id = u.user_id WHERE wiki_id = "
            + wikiId + " AND u.user_name = '" + username + "'");
    List<Map<String, Object>> userId = jdbc.queryForList(
        "SELECT user_id FROM users WHERE user_name = '" + username + "'");
    if (check.isEmpty()) {
      jdbc.execute("INSERT INTO wiki_user_berechtigung SET wiki_id = '" + wikiId + "', user_id = '"
          + userId.get(0).get("user_id") + "', berechtigung = '" + wikiRight + "'");
    }
    jdbc.execute(
        "UPDATE wiki_user_berechtigung SET berechtigung = '" + wikiRight + "' WHERE wiki_id = "
            + wikiId + " AND user_id = " + userId.get(0).get("user_id") + ";");
    if (recursive) {
      List<Map<String, Object>> contents = wikiJdbcService.getContents(wikiId);
      List<Map<String, Object>> topics = wikiJdbcService.getTopics(wikiId);
      for (Map<String, Object> content : contents) {
        addWikiUserRight(Integer.parseInt(content.get("id").toString()), username, wikiRight,
            false);
      }
      for (Map<String, Object> topic : topics) {
        addWikiUserRight(Integer.parseInt(topic.get("id").toString()), username, wikiRight,
            true);
      }
    }
  }

  //Die Berechtigung eines Benutzers auf eine WikiID entfernen
  @Override
  public void deleteWikiUserRight(int wikiId, String username) {
    List<Map<String, Object>> userId = jdbc.queryForList(
        "SELECT user_id FROM users WHERE user_name = '" + username + "'");
    jdbc.execute("DELETE FROM wiki_user_berechtigung WHERE wiki_id = " + wikiId + " AND user_id = "
        + userId.get(0).get("user_id") + "");

    List<Map<String, Object>> contents = wikiJdbcService.getContents(wikiId);
    for (Map<String, Object> content : contents) {
      deleteWikiUserRight(Integer.parseInt(content.get("id").toString()), username);
    }
    if (Integer.parseInt(wikiJdbcService.getContent(wikiId).get(0).get("type").toString()) == 1) {
      List<Map<String, Object>> topics = wikiJdbcService.getTopics(wikiId);
      for (Map<String, Object> topic : topics) {
        deleteWikiUserRight(Integer.parseInt(topic.get("id").toString()), username);
      }
    }
  }

  //Einer Rolle eine Berechtigung auf eine WikiID hinzufügen oder aktuallisieren
  @Override
  public void addWikiRoleRight(int wikiId, int role, String wikiRight, boolean recursive) {
    List<Map<String, Object>> check = jdbc.queryForList(
        "SELECT * FROM wiki_role_berechtigung WHERE wiki_id = " + wikiId + " AND role_id = " + role
            + ";");
    if (check.isEmpty()) {
      jdbc.execute(
          "INSERT INTO wiki_role_berechtigung SET wiki_id = '" + wikiId + "', role_id = '" + role
              + "', berechtigung = '" + wikiRight + "'");
    }
    jdbc.execute(
        "UPDATE wiki_role_berechtigung SET berechtigung = '" + wikiRight + "' WHERE wiki_id = "
            + wikiId + " AND role_id = " + role + ";");
    if (recursive) {
      List<Map<String, Object>> contents = wikiJdbcService.getContents(wikiId);
      List<Map<String, Object>> topics = wikiJdbcService.getTopics(wikiId);
      for (Map<String, Object> content : contents) {
        addWikiRoleRight(Integer.parseInt(content.get("id").toString()), role, wikiRight, false);
      }
      for (Map<String, Object> topic : topics) {
        addWikiRoleRight(Integer.parseInt(topic.get("id").toString()), role, wikiRight, true);
      }
    }
  }

  //Default Read Rolle einer WikiID ändern
  @Override
  public void setWikiDefaultRead(int wikiId, int roleId) {
    int check = getWikiDefaultRead(wikiId);
    if (check == 3) {
      jdbc.execute(
          "INSERT INTO wiki_default_read SET wiki_id = '" + wikiId + "', role_id = '" + roleId
              + "';");
    } else {
      jdbc.execute(
          "UPDATE wiki_default_read SET role_id = '" + roleId + "' WHERE wiki_id = " + wikiId
              + ";");
    }
  }

  //Default Read Rolle einer WikiID löschen (zurücksetzen auf Default -> Role ID 3 -> User)
  @Override
  public void deleteWikiDefaultRead(int wikiId) {
    jdbc.execute("DELETE FROM wiki_default_read WHERE wiki_id  = " + wikiId + ";");
  }

  //Alle Berechtigungen auf eine wiki ID entfernen
  @Override
  public void deleteWikiAllRights(int wikiId) {
    jdbc.execute("DELETE FROM wiki_user_berechtigung WHERE wiki_id = " + wikiId + ";");
    jdbc.execute("DELETE FROM wiki_role_berechtigung WHERE wiki_id = " + wikiId + ";");
  }

  //Kopiert die Rechte für ein Themas und setzt diese für die übergebene WikiID
  @Override
  public void setWikiRightsCopyOvertopic(int wikiId, int overtopicId) {
    List<Map<String, Object>> users = userAdapter.getWikiUsersWithRights(overtopicId);
    List<Map<String, Object>> roles = roleAdapter.getWikiRolesWithRights(overtopicId);
    int drr = getWikiDefaultRead(overtopicId);
    for (Map<String, Object> user : users) {
      addWikiUserRight(wikiId, user.get("username").toString(), user.get("wikiRight").toString(),
          false);
    }
    for (Map<String, Object> role : roles) {
      addWikiRoleRight(wikiId, Integer.parseInt(role.get("id").toString()),
          role.get("wikiRight").toString(), false);
    }
    if (drr == 3) {
      deleteWikiDefaultRead(wikiId);
    } else {
      setWikiDefaultRead(wikiId, drr);
    }
  }

}
