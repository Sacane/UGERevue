package fr.pentagon.ugeoverflow.config.authorization;

public enum Role {
  USER, ADMIN;

  public String authorityName() {
    return "ROLE_" + name();
  }

  public String roleName() {
    return name();
  }
  public String displayName() {
    return this == USER ? "Utilisateur" : "Administrateur";
  }
}