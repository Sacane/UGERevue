package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.usersOf LEFT JOIN FETCH t.reviewsOf WHERE t.name = :name")
    Optional<Tag> findTagByName(@Param("name") String name);

    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.usersOf u WHERE u.id = :userId")
    List<Tag> findByUser(@Param("userId") long userId);

    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.usersOf WHERE t.name = :name")
    Optional<Tag> findTagByNameWithUsers(@Param("name") String name);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    List<Tag> findAllByNames(@Param("names") List<String> names);
}
