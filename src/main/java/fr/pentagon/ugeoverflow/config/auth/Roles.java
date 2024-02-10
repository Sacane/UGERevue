package fr.pentagon.ugeoverflow.config.auth;

public enum Roles {
  USER("ROLE_USER"), ADMIN("ROLE_ADMIN");
  private final String name;

  Roles(String name) {
    this.name = name;
  }

  public String roleName() {
    return name;
  }
}