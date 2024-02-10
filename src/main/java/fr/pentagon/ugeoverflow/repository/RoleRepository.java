package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
