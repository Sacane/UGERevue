package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue
  private long id;
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
}
