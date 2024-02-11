package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue
  private long id;

  @Column(unique = true)
  private String name;

  public Role() {
  }

  public Role(String name) {
    this.name = Objects.requireNonNull(name);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Role role && name.equals(role.name);
  }
}
