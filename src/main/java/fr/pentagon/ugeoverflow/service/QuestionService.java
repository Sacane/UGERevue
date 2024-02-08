package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.vote.QuestionVote;
import fr.pentagon.ugeoverflow.model.vote.QuestionVoteId;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionVoteRepository questionVoteRepository;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository, ReviewRepository reviewRepository, QuestionVoteRepository questionVoteRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.questionVoteRepository = questionVoteRepository;
    }

    @Transactional
    public ResponseEntity<List<ReviewResponseChildrenDTO>> getReviews(long questionId) {
        var questionOptional = questionRepository.findByIdWithReviews(questionId);
        if (questionOptional.isEmpty()) {
            throw HttpException.notFound("Question not exist");
        }
        var question = questionOptional.get();
        var author = question.getAuthor();

        return ResponseEntity.ok(question.getReviews().stream().map(review -> {
            var lineContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");
            var citedCode = IntStream.range(review.getLineStart() - 1, review.getLineEnd())
                    .mapToObj(i -> lineContent[i])
                    .collect(Collectors.joining("\n"));

            return new ReviewResponseChildrenDTO(author.getId(), author.getUsername(), review.getId(), review.getContent(), citedCode, List.of());
        }).toList());
    }

    @Transactional
    public ResponseEntity<Long> create(QuestionCreateDTO questionCreateDTO) {
        var userFind = userRepository.findById(questionCreateDTO.userId());

        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var user = userFind.get();
        var question = questionRepository.save(new Question(questionCreateDTO.title(), questionCreateDTO.descrition(), questionCreateDTO.file(), questionCreateDTO.testFile(), "TEST RESULT", true, new Date()));
        user.addQuestion(question);

        return ResponseEntity.ok(question.getId());
    }

    @Transactional
    public ResponseEntity<Long> addReview(QuestionReviewCreateDTO questionReviewCreateDTO) {
        var userFind = userRepository.findById(questionReviewCreateDTO.userId());
        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var questionFind = questionRepository.findById(questionReviewCreateDTO.questionId());
        if (questionFind.isEmpty()) {
            throw HttpException.notFound("Question not exist");
        }
        var user = userFind.get();
        var question = questionFind.get();

        var review = reviewRepository.save(new Review(questionReviewCreateDTO.content(), questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd(), new Date()));
        question.addReview(review);
        user.addReview(review);

        return ResponseEntity.ok(review.getId());
    }

    @Transactional
    public ResponseEntity<Void> vote(long authorId, long questionId, boolean isUpVote) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("User does not exists"));
        var question = questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("Question does not exists"));
        var vote = (isUpVote) ? QuestionVote.upvote(user, question) : QuestionVote.downVote(user, question);
        questionVoteRepository.save(vote);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> cancelVote(long authorId, long questionId) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("User does not exists"));
        var question = questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("Question does not exists"));
        var questionVoteId = new QuestionVoteId();
        questionVoteId.setQuestion(question);
        questionVoteId.setAuthor(user);
        questionVoteRepository.deleteById(questionVoteId);
        return ResponseEntity.ok().build();
    }
}
