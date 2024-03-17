package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.algorithm.QuestionSorterStrategy;
import fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.TestResultDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.model.embed.CodePart;
import fr.pentagon.ugeoverflow.model.vote.QuestionVote;
import fr.pentagon.ugeoverflow.repository.*;
import fr.pentagon.ugeoverflow.service.mapper.QuestionMapper;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final Logger logger = Logger.getLogger(QuestionService.class.getName());
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionVoteRepository questionVoteRepository;
    private final TagRepository tagRepository;
    private final WebClient webClient;
    private final QuestionMapper questionMapper;

    public QuestionService(
            ReviewService reviewService,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            QuestionVoteRepository questionVoteRepository,
            WebClient webClient,
            QuestionMapper questionMapper,
            TagRepository tagRepository
    ) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.questionVoteRepository = questionVoteRepository;
        this.questionMapper = questionMapper;
        this.webClient = webClient;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public List<QuestionDTO> getQuestions() {
        return questionRepository.findAllWithAuthors()
                .stream()
                .map(questionMapper::entityToQuestionDTO)
                .toList();
    }

    @Transactional
    public List<QuestionDTO> getQuestions(String label, String username) {
        Objects.requireNonNull(label);
        var questions = questionRepository.findAllWithAuthors();
        QuestionSorterStrategy questionSorterStrategy = new SearchQuestionByLabelStrategy();

        return questionSorterStrategy.getQuestions(label, (username != null) ? QuestionSorterStrategy.WITH_AUTHOR.getQuestions(username, questions) : questions)
                .stream()
                .map(questionMapper::entityToQuestionDTO)
                .toList();
    }

    @Transactional
    public long create(NewQuestionDTO questionCreateDTO, long authorId) {
        var user = userRepository.findById(authorId)
                .orElseThrow(() -> HttpException.notFound("User not exist"));
        String result = "Test failed...";
        if (questionCreateDTO.testFile() != null) {
            try {
                var parts = getPartsTestEndpoints(questionCreateDTO, authorId);
                var response = webClient.post()
                        .uri(builder -> builder.path("/tests/run").build())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(parts))
                        .accept(MediaType.APPLICATION_JSON).exchangeToMono(r -> {
                            if (r.statusCode().is2xxSuccessful()) {
                                return r.bodyToMono(TestResultDTO.class);
                            } else {
                                logger.severe(r.statusCode().value() + "");
                                return Mono.just(TestResultDTO.zero());
                            }
                        }).block();
                if (response != null) {
                    result = response.toString();
                }
            } catch (WebClientRequestException ignored) {/*Serveur non disponible pour lancer les tests*/}
        }
        var question = questionRepository.save(new Question(questionCreateDTO.title(), questionCreateDTO.description(), questionCreateDTO.javaFile(), questionCreateDTO.testFile(), result, true, new Date())); //TODO test
        user.addQuestion(question);
        return question.getId();
    }

    private static MultiValueMap<String, Object> getPartsTestEndpoints(NewQuestionDTO questionCreateDTO, long authorId) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("dependencyFile", new ByteArrayResource(questionCreateDTO.javaFile()));
        parts.add("id", authorId);
        parts.add("testFile", new ByteArrayResource(questionCreateDTO.testFile()));
        parts.add("dependencyFilename", questionCreateDTO.javaFilename());
        parts.add("testFilename", questionCreateDTO.testFilename());
        return parts;
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    public void update(QuestionUpdateDTO questionUpdateDTO) {
        var userQuestion = QuestionService.findQuestionFromId(userRepository, questionUpdateDTO.userId(), questionRepository, questionUpdateDTO.questionId());
        var user = userQuestion.user();
        var question = userQuestion.question();

        if (!userRepository.containsQuestion(user.getId(), question)) {
            throw HttpException.unauthorized("Not your question");
        }

        if (questionUpdateDTO.title() != null) {
            question.setTitle(questionUpdateDTO.title());
        }
        if (questionUpdateDTO.description() != null) {
            question.setDescription(questionUpdateDTO.description());
        }
        if (questionUpdateDTO.file() != null) {
            question.setFile(questionUpdateDTO.file());
        }
        if (questionUpdateDTO.testFile() != null) {
            question.setTestFile(questionUpdateDTO.testFile());
            question.setTestResult("TEST RESULT"); //TODO test
        }
    }

    record UserQuestion(User user, Question question) {
    }

    static UserQuestion findQuestionFromId(UserRepository userRepository, long userId, QuestionRepository questionRepository, long questionId) {
        var userFind = userRepository.findById(userId)
                .orElseThrow(() -> HttpException.notFound("The user number " + userId + " does not exists"));
        var question = questionRepository.findById(questionId)
                .orElseThrow(() -> HttpException.notFound("The question number " + questionId + " does not exists"));
        return new UserQuestion(userFind, question);
    }

    @Transactional
    public ReviewQuestionResponseDTO addReview(QuestionReviewCreateDTO questionReviewCreateDTO) {
        var userQuestion = QuestionService.findQuestionFromId(userRepository, questionReviewCreateDTO.userId(), questionRepository, questionReviewCreateDTO.questionId());
        var user = userQuestion.user();
        var question = userQuestion.question();
        var codePart = (questionReviewCreateDTO.lineStart() == null || questionReviewCreateDTO.lineEnd() == null)
                ? null
                : new CodePart(questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd());
        var review = reviewRepository.save(new Review(questionReviewCreateDTO.content(), codePart, new Date()));
        question.addReview(review);
        user.addReview(review);

        var fileContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");
        var lineStart = questionReviewCreateDTO.lineStart();
        var lineEnd = questionReviewCreateDTO.lineEnd();
        String citedCode = null;
        if (lineStart != null && lineEnd != null && lineStart > 0 && lineEnd <= fileContent.length) {
            citedCode = Arrays.stream(fileContent, lineStart - 1, lineEnd).collect(Collectors.joining("\n"));
        }

        addTags(questionReviewCreateDTO, user, review);
        return new ReviewQuestionResponseDTO(
                review.getId(),
                user.getUsername(),
                review.getCreatedAt(),
                review.getContent(),
                citedCode,
                0,
                0,
                List.of()
        );
    }

    private void addTags(QuestionReviewCreateDTO questionReviewCreateDTO, User user, Review review) {
        questionReviewCreateDTO.tagList().forEach(tag -> {
            var existingTagOptional = tagRepository.findTagByName(tag);
            if (existingTagOptional.isEmpty()) {
                var newTag = new Tag(tag);
                tagRepository.save(newTag);
                user.addTag(newTag);
                review.addTag(newTag);
            } else {
                var existingTag = existingTagOptional.get();
                user.addTag(existingTag);
                review.addTag(existingTag);
            }
        });
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

        var containsQuestion = userRepository.containsQuestion(user.getId(), question);
        if (!containsQuestion && user.getRole() != Role.ADMIN) {
            throw HttpException.unauthorized("Not your question");
        }
        if (containsQuestion) {
            user.removeQuestion(question);
        }
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
                voteCount,
                question.getReviews().size()

        );
    }

    @Transactional
    public List<QuestionDTO> getQuestionsFromCurrentUser(String login) {
        var user = userRepository.findByLogin(login).orElseThrow();
        return questionRepository.findByAuthorOrderByCreatedAtDesc(user)
                .stream()
                .map(questionMapper::entityToQuestionDTO)
                .toList();
    }

    @Transactional
    public List<QuestionDTO> getQuestionsFromFollowers(long userId) {
        var questions = questionRepository.findAll();
        var questionsWithScore = new HashMap<Question, Integer>();
        var visitedUserId = new ArrayList<Long>();

        questions.forEach(question -> questionsWithScore.put(question, 0));

        visitQuestionWithScore(userId, visitedUserId, questionsWithScore, 1);

        return questionsWithScore.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(entrySet -> {
            var question = entrySet.getKey();

            return new QuestionDTO(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getAuthor().getUsername(),
                    question.getCreatedAt().toString(),
                    questionVoteRepository.countAllById(question.getId()),
                    question.getReviews().size()
            );
        }).toList();
    }

    private void visitQuestionWithScore(long userId, List<Long> visitedUserId, Map<Question, Integer> questionsWithScore, int score) {
        var follows = userRepository.findFollowsById(userId);

        visitedUserId.add(userId);
        for (var follow : follows) {
            for (var question : follow.getQuestions()) {
                var questionScore = questionsWithScore.get(question);
                if (questionScore == 0 || score < questionScore) {
                    questionsWithScore.put(question, score);
                }
            }

            if (!visitedUserId.contains(follow.getId())) {
                visitQuestionWithScore(follow.getId(), visitedUserId, questionsWithScore, score + 1);
            }
        }
    }
}
