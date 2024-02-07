package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.vote.ReviewVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, ReviewVoteId> {
    @Query(value = """
            SELECT COUNT(*) FROM "VOTE_QUESTION" v
            WHERE v.question_id = :questionId AND
            v.is_up = true
            """,
            nativeQuery = true)
    long findUpvoteNumberByQuestionId(long questionId);

    @Query(value = """
            SELECT COUNT(*) FROM "VOTE_QUESTION" v
            WHERE v.question_id = :questionId AND
            v.is_up = false
            """,
            nativeQuery = true)
    long findDownvoteNumberByQuestionId(long questionId);
}
