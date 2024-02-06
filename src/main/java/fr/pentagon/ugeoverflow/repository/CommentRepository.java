package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
