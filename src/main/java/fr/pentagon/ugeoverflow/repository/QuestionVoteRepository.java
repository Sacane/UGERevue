package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.vote.QuestionVote;
import fr.pentagon.ugeoverflow.model.vote.QuestionVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionVoteRepository extends JpaRepository<QuestionVote, QuestionVoteId> {

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


    @Query(value = """
    SELECT COUNT(*) FROM "VOTE_QUESTION" v WHERE v.question_id = :id
    """, nativeQuery = true)
    long countAllById(long id);
}
