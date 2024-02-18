package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsDTO;
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
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionServiceWithFailure questionServiceWithFailure;
    private final ReviewService reviewService;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionVoteRepository questionVoteRepository;

    public QuestionService(
            QuestionServiceWithFailure questionServiceWithFailure,
            ReviewService reviewService,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            QuestionVoteRepository questionVoteRepository
    ) {
        this.questionServiceWithFailure = questionServiceWithFailure;
        this.reviewService = reviewService;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.questionVoteRepository = questionVoteRepository;
    }

    @Transactional
    public List<QuestionDTO> getQuestions() {
        return questionRepository.findAllWithAuthors()
                .stream()
                .map(question -> new QuestionDTO(
                        question.getId(),
                        question.getTitle(),
                        question.getDescription(),
                        question.getAuthor().getUsername(),
                        question.getCreatedAt().toString(),
                        questionVoteRepository.countAllById(question.getId()),
                        question.getReviews().size()
                    )).toList();
    }

    @Transactional
    public long create(NewQuestionDTO questionCreateDTO, long authorId) {
        var user = userRepository.findById(authorId)
                .orElseThrow(() -> HttpException.notFound("User not exist"));
        var question = questionRepository.save(new Question(questionCreateDTO.title(), questionCreateDTO.description(), questionCreateDTO.javaFile(), questionCreateDTO.testFile(), "TEST RESULT", true, new Date())); //TODO test
        user.addQuestion(question);

        return question.getId();
    }

    public void update(QuestionUpdateDTO questionUpdateDTO) {
        var retry = true;

        while (retry) {
            retry = false;

            try {
                questionServiceWithFailure.update(questionUpdateDTO);
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                retry = true;
            }
        }
    }

    @Transactional
    public long addReview(QuestionReviewCreateDTO questionReviewCreateDTO) {
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

        return review.getId();
    }

    @Transactional
    public void remove(QuestionRemoveDTO questionRemoveDTO) {
        var userFind = userRepository.findById(questionRemoveDTO.userId());
        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var questionFind = questionRepository.findById(questionRemoveDTO.questionId());
        if (questionFind.isEmpty()) {
            throw HttpException.notFound("Question not exist");
        }
        var user = userFind.get();
        var question = questionFind.get();

        if (!userRepository.containsQuestion(user.getId(), question)) {
            throw HttpException.unauthorized("Not your question");
        }
        user.removeQuestion(question);
        questionRepository.delete(question);
    }

    @Transactional
    public void vote(long authorId, long questionId, boolean isUpVote) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("User does not exists"));
        var question = questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("Question does not exists"));
        var vote = (isUpVote) ? QuestionVote.upvote(user, question) : QuestionVote.downVote(user, question);
        questionVoteRepository.save(vote);
    }

    @Transactional
    public void cancelVote(long authorId, long questionId) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("User does not exists"));
        var question = questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("Question does not exists"));
        var questionVoteId = new QuestionVoteId();
        questionVoteId.setQuestion(question);
        questionVoteId.setAuthor(user);
        questionVoteRepository.deleteById(questionVoteId);
    }

    @Transactional
    public QuestionDetailsDTO findById(long questionId) {
        var question = questionRepository.findByIdWithAuthorAndReviews(questionId).orElseThrow(() -> HttpException.notFound("Question " + questionId + " does not exists"));
        var dateFormatter = new DateFormatter("dd/MM/yyyy");
        var voteCount = questionVoteRepository.countAllById(questionId);
        return new QuestionDetailsDTO(
                question.getId(),
                question.getAuthor().getUsername(),
                dateFormatter.print(question.getCreatedAt(), Locale.FRANCE),
                List.of(),
                question.getTitle(),
                question.getDescription(),
                new String(question.getFile(), StandardCharsets.UTF_8),
                question.getTestFile() != null ? new String(question.getTestFile(), StandardCharsets.UTF_8) : null,
                "",
                question.getReviews().size(),
                voteCount
        );
    }
}
