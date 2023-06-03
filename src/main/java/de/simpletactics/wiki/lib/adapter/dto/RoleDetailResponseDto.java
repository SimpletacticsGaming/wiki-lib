package de.simpletactics.wiki.lib.adapter.dto;

public class RoleDetailResponseDto {

  private int id;
  private String role;
  private boolean deletable;
  private boolean defaultRole;

  public int getId() {
    return id;
  }

  public String getRole() {
    return role;
  }

  public boolean getDeletable() {
    return deletable;
  }

  public boolean getDefaultRole() {
    return defaultRole;
  }
}
