package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.algorithm.QuestionSorterStrategy;
import fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.TestResultDTO;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class QuestionService {

  private final Logger logger = Logger.getLogger(QuestionService.class.getName());

  private final QuestionServiceWithFailure questionServiceWithFailure;
  private final ReviewService reviewService;
  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final QuestionVoteRepository questionVoteRepository;
  private final WebClient webClient;

  public QuestionService(
          QuestionServiceWithFailure questionServiceWithFailure,
          ReviewService reviewService,
          QuestionRepository questionRepository,
          UserRepository userRepository,
          ReviewRepository reviewRepository,
          QuestionVoteRepository questionVoteRepository,
          WebClient webClient
  ) {
    this.questionServiceWithFailure = questionServiceWithFailure;
    this.reviewService = reviewService;
    this.questionRepository = questionRepository;
    this.userRepository = userRepository;
    this.reviewRepository = reviewRepository;
    this.questionVoteRepository = questionVoteRepository;
      this.webClient = webClient;
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
    public List<QuestionDTO> getQuestions(String label, String username) {
        Objects.requireNonNull(label);
        var questions = questionRepository.findAllWithAuthors();
        QuestionSorterStrategy questionSorterStrategy = new SearchQuestionByLabelStrategy();

        return questionSorterStrategy.getQuestions(label, (username != null) ? QuestionSorterStrategy.WITH_AUTHOR.getQuestions(username, questions) : questions)
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
    String result = "Test failed...";
    if(questionCreateDTO.testFile() != null) {
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
  public ReviewQuestionResponseDTO addReview(QuestionReviewCreateDTO questionReviewCreateDTO) {
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

    var fileContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");
    var lineStart = questionReviewCreateDTO.lineStart();
    var lineEnd = questionReviewCreateDTO.lineEnd();
    String citedCode = null;
    if (lineStart != null && lineEnd != null && lineStart > 0 && lineEnd <= fileContent.length) {
      citedCode = Arrays.stream(fileContent, lineStart - 1, lineEnd).collect(Collectors.joining("\n"));
    }

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

  @Transactional
  public List<QuestionDTO> getQuestionsFromCurrentUser(String login) {
    var user = userRepository.findByLogin(login).orElseThrow();
    return questionRepository.findByAuthorOrderByCreatedAtDesc(user)
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
}
