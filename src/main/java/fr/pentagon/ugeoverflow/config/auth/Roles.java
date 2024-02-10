package fr.pentagon.ugeoverflow.config.auth;

public enum Roles {
  USER, ADMIN;

  public String roleName() {
    return "ROLE_" + name();
  }
}