package de.simpletactics.wiki.lib.adapter.persistence;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoleAdapter implements RolePort {

  private final JdbcTemplate jdbc;
  @Autowired
  private WikiEntryAdapter wikiJdbcService;

  public RoleAdapter(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  //gibt den Namen einer Rolle anhand ihrer ID zurück
  @Override
  public String getRoleName(int id) {
    return jdbc.queryForList("SELECT role FROM roles WHERE role_id = " + id + "").get(0).get("role")
        .toString();
  }

  //gibt alle rollen aus, oder wenn filter true dann nur die nicht Standard Rollen
  @Override
  public List<Map<String, Object>> getRoles(boolean filter) {
    return filter ? jdbc.queryForList(
        "SELECT role_id AS id, role AS role FROM roles WHERE role_id > 10")
        : jdbc.queryForList("SELECT role_id AS id, role AS role FROM roles;");
  }

  @Override
  public List<Map<String, Object>> getDefaultRoles() {
    return jdbc.queryForList(
        "SELECT role_id AS id, role AS role FROM roles WHERE role_id < 11;");
  }

  @Override
  public List<Map<String, Object>> getNoneDefaultRoles() {
    return jdbc.queryForList(
        "SELECT role_id AS id, role AS role FROM roles WHERE role_id > 10;");
  }


  @Override
  public List<Map<String, Object>> getUserDefaultRole(int userId) {
    return jdbc.queryForList(
        "SELECT r.role_id AS id, r.role AS role FROM roles AS r INNER JOIN user_role as ur "
            + "ON r.role_id = ur.role_id WHERE ur.user_id = "
            + userId + " AND r.default_role = true;");
  }

  //Die Berechtigung einer Rolle auf eine WikiID entfernen
  @Override
  public void deleteWikiRoleRight(int wikiId, int role) {
    jdbc.execute(
        "DELETE FROM wiki_role_berechtigung WHERE wiki_id = " + wikiId + " AND role_id = " + role
            + "");
    List<Map<String, Object>> contents = wikiJdbcService.getContents(wikiId);
    for (Map<String, Object> content : contents) {
      deleteWikiRoleRight(Integer.parseInt(content.get("id").toString()), role);
    }
    if (Integer.parseInt(wikiJdbcService.getContent(wikiId).get(0).get("type").toString()) == 1) {
      List<Map<String, Object>> topics = wikiJdbcService.getTopics(wikiId);
      for (Map<String, Object> topic : topics) {
        deleteWikiRoleRight(Integer.parseInt(topic.get("id").toString()), role);
      }
    }
  }

  //gibt die Berechtigung einer Rolle auf einen Wiki-Eintrag zurück
  @Override
  public String getRoleWikiRights(int roleId, int wikiId) {
    List<Map<String, Object>> lBerechtigung = jdbc.queryForList(
        "SELECT berechtigung FROM wiki_role_berechtigung WHERE wiki_id = " + wikiId
            + " && role_id = " + roleId + ";");
    String berechtigung = "";
    if (!lBerechtigung.isEmpty()) {
      berechtigung = lBerechtigung.get(0).get("berechtigung").toString();
    }
    return berechtigung;
  }

  @Override
  public List<Map<String, Object>> getWikiRolesWithRights(int wikiId) {
    return jdbc.queryForList(
        "SELECT r.role_id AS id, r.role AS role, wub.berechtigung AS berechtigung "
            + "FROM wiki_role_berechtigung AS wub INNER JOIN roles AS r ON wub.role_id = r.role_id "
            + "WHERE wub.wiki_id =" + wikiId + ";");
  }

  @Override
  public void setUserRole(int userId, int roleId) {
    jdbc.execute("INSERT INTO user_role VALUES (" + userId + ", '" + roleId + "')");
  }

  @Override
  public void updateRole(RoleDetailResponseDto roleDetail) {
    jdbc.execute(
        "UPDATE roles SET role_id = " + roleDetail.getId() + ", role = '" + roleDetail.getRole()
            + "', deletable =  " + roleDetail.getDeletable() + ", default_role = "
            + roleDetail.getDefaultRole()
            + " WHERE role_id = " + roleDetail.getId() + ";");
  }

  @Override
  public void deleteUserRole(int userId, int roleId) {
    jdbc.execute(
        "DELETE FROM user_role WHERE user_id=" + userId + " AND role_id = " + roleId + ";");
  }

  @Override
  public void updateUserDefaultRole(int userId, int oldRoleId, int newRoleId) {
    jdbc.execute(
        "UPDATE user_role SET role_id = " + newRoleId + " WHERE user_id = " + userId
            + " AND role_id = " + oldRoleId + ";");
  }

  @Override
  public void createRole(String roleName) {
    jdbc.execute("INSERT INTO roles VALUES (null, '" + roleName + "', true, false);");
  }

  @Override
  public void deleteRole(int roleId) {
    if (isRoleDeletable(roleId)) {
      jdbc.execute("DELETE FROM user_role WHERE role_id = " + roleId + ";");
      jdbc.execute("DELETE FROM wiki_default_read WHERE role_id = " + roleId + ";");
      jdbc.execute("DELETE FROM wiki_role_berechtigung WHERE role_id = " + roleId + ";");
      jdbc.execute("DELETE FROM roles WHERE role_id = " + roleId + ";");
    }
  }

  private boolean isRoleDeletable(int roleId) {
    return CollectionUtils
        .isNotEmpty(jdbc.queryForList(
            "SELECT * FROM roles WHERE deletable = true AND role_id = " + roleId + ";"));
  }

  @Override
  public List<Map<String, Object>> getAllRolesForUser(int userId) {
    return jdbc.queryForList(
        "SELECT r.role AS role, r.role_id as id, r.deletable as deletable, r.default_role as defaultRole "
            + "FROM user_role ur INNER JOIN roles r ON ur.role_id = r.role_id "
            + "INNER JOIN users u ON ur.user_id = u.user_id "
            + "WHERE u.user_id = " + userId + ";");
  }


  @Override
  public List<Map<String, Object>> findAllOrderByDefault() {
    return jdbc.queryForList(
        "SELECT * "
            + "FROM roles "
            + "ORDER BY default_role DESC;"
    );
  }

  @Override
  public List<Map<String, Object>> findAllUsersForRole(int roleId) {
    return jdbc.queryForList(
        "SELECT u.user_id AS id, u.user_name AS username, u.first_name AS firstname, u.last_name AS lastname, u.e_mail AS email "
            + "FROM user_role ur INNER JOIN roles r ON ur.role_id = r.role_id "
            + "INNER JOIN users u ON ur.user_id = u.user_id "
            + "WHERE ur.role_id = " + roleId + ";"
    );
  }
}
