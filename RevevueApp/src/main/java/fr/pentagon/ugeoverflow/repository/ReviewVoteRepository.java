package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.vote.ReviewVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, ReviewVoteId> {
    @Query(value = """
            SELECT COUNT(*) FROM "REVIEW_VOTE" v
            WHERE v.review_id = :reviewId AND
            v.is_up = true
            """,
            nativeQuery = true)
    long findUpvoteNumberByReviewId(@Param("reviewId") long reviewId);

    @Query(value = """
            SELECT COUNT(*) FROM "REVIEW_VOTE" v
            WHERE v.review_id = :reviewId AND
            v.is_up = false
            """,
            nativeQuery = true)
    long findDownvoteNumberByReviewId(@Param("reviewId") long reviewId);

    @Query(value = """
            SELECT is_up FROM "REVIEW_VOTE" v
            WHERE v.review_id = :reviewId AND v.author_id = :userId
            """,
            nativeQuery = true)
    Optional<Boolean> findVoteUserByReviewId(@Param("userId") long userId, @Param("reviewId") long reviewId);

    @Query(value = """
            SELECT * FROM "REVIEW_VOTE" v
            WHERE v.review_id = :reviewId
            """,
            nativeQuery = true)
    List<ReviewVote> findAllVoteByReviewId(@Param("reviewId") long reviewId);
}
