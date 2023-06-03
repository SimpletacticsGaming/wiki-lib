package de.simpletactics.wiki.lib.service.port;

import de.simpletactics.wiki.lib.adapter.dto.RoleDetailResponseDto;
import java.util.List;
import java.util.Map;

public interface RolePort {

  // TODO: nicht verwendet löschen?
  String getRoleName(int id);

  List<Map<String, Object>> getRoles(boolean filter);

  List<Map<String, Object>> getDefaultRoles();

  List<Map<String, Object>> getNoneDefaultRoles();

  List<Map<String, Object>> getUserDefaultRole(int userId);

  void deleteWikiRoleRight(int wikiId, int role);

  String getRoleWikiRights(int roleId, int wikiId);

  List<Map<String, Object>> getWikiRolesWithRights(int wikiId);

  void createRole(String roleName);

  void deleteRole(int roleId);

  void setUserRole(int userId, int roleId);

  void updateRole(RoleDetailResponseDto roleDetail);

  void deleteUserRole(int userId, int roleId);

  void updateUserDefaultRole(int userId, int oldRoleId, int newRoleId);

  List<Map<String, Object>> getAllRolesForUser(int userId);

  List<Map<String, Object>> findAllOrderByDefault();

  List<Map<String, Object>> findAllUsersForRole(int roleId);
}
