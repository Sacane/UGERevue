package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.algorithm.QuestionSorterStrategy;
import fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy;
import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.*;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.model.embed.CodePart;
import fr.pentagon.ugeoverflow.model.vote.QuestionVote;
import fr.pentagon.ugeoverflow.model.vote.QuestionVoteId;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.mapper.QuestionMapper;
import jakarta.transaction.Transactional;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

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
  private final TagService tagRepository;
  private final QuestionMapper questionMapper;
  private final TestServiceRunner testServiceRunner;
  private final ReviewService reviewService;

  public QuestionService(
      QuestionRepository questionRepository,
      UserRepository userRepository,
      ReviewRepository reviewRepository,
      QuestionVoteRepository questionVoteRepository,
      QuestionMapper questionMapper,
      TagService tagRepository,
      TestServiceRunner testServiceRunner,
      ReviewService reviewService
  ) {
    this.questionRepository = questionRepository;
    this.userRepository = userRepository;
    this.reviewRepository = reviewRepository;
    this.questionVoteRepository = questionVoteRepository;
    this.questionMapper = questionMapper;
    this.tagRepository = tagRepository;
    this.testServiceRunner = testServiceRunner;
    this.reviewService = reviewService;
  }

  static UserQuestion findQuestionFromId(UserRepository userRepository, long userId, QuestionRepository questionRepository, long questionId) {
    var userFind = userRepository.findById(userId)
        .orElseThrow(() -> HttpException.notFound("The user number " + userId + " does not exists"));
    var question = questionRepository.findById(questionId)
        .orElseThrow(() -> HttpException.notFound("The question number " + questionId + " does not exists"));
    return new UserQuestion(userFind, question);
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
        .orElseThrow(() -> HttpException.notFound("User does not exists"));
    String testFilename = questionCreateDTO.testFilename();
    if (!questionCreateDTO.javaFilename().endsWith(".java") || (testFilename != null && !testFilename.isBlank() && !testFilename.endsWith(".java"))) {
      throw HttpException.badRequest("Le fichier enregistré n'est pas un fichier java.");
    }
    String result;
    if (questionCreateDTO.testFile() != null && testFilename != null && !testFilename.isBlank()) {
      result = testServiceRunner.sendTestAndGetFeedback(
          questionCreateDTO.javaFilename(),
          questionCreateDTO.testFilename(),
          questionCreateDTO.javaFile(),
          questionCreateDTO.testFile(),
          authorId
      );
    } else {
      result = "Pas de fichier de test fourni...";
    }
    var question = questionRepository.save(new Question(questionCreateDTO.title(), questionCreateDTO.description(), questionCreateDTO.javaFile(), questionCreateDTO.testFile(), result, true, new Date()));
    question.setFileName(questionCreateDTO.javaFilename());
    if (questionCreateDTO.testFile() != null) {
      question.setTestFileName(questionCreateDTO.testFilename());
    }
    user.addQuestion(question);
    return question.getId();
  }

  @Transactional
  @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
  public QuestionUpdateResponseDTO update(long userId, long questionId, QuestionUpdateDTO questionUpdateDTO) {
    var userQuestion = findQuestionFromId(userRepository, userId, questionRepository, questionId);
    var user = userQuestion.user();
    var question = userQuestion.question();

    if (!userRepository.containsQuestion(user.getId(), question)) {
      throw HttpException.unauthorized("Not your question");
    }

    if (questionUpdateDTO.description() != null) {
      question.setDescription(questionUpdateDTO.description());
    }
    if (questionUpdateDTO.testFile() != null) {

      question.setTestFile(questionUpdateDTO.testFile());
      question.setTestFileName(questionUpdateDTO.testFilename());
      var result = testServiceRunner.sendTestAndGetFeedback(
          question.getFileName(),
          question.getTestFileName(),
          question.getFile(),
          question.getTestFile(),
          userId
      );
      question.setTestResult(result);

    }

    return new QuestionUpdateResponseDTO(
        questionUpdateDTO.description(),
        question.getTestFile() != null ? new String(question.getTestFile(), StandardCharsets.UTF_8) : null,
        question.getTestFile() != null ? question.getTestResult() : null
    );
  }

  @Transactional
  public ReviewQuestionResponseDTO addReview(QuestionReviewCreateDTO questionReviewCreateDTO) {
    var userQuestion = QuestionService.findQuestionFromId(userRepository, questionReviewCreateDTO.userId(), questionRepository, questionReviewCreateDTO.questionId());
    var user = userQuestion.user();
    var question = userQuestion.question();
    var lineStart = questionReviewCreateDTO.lineStart();
    var fileContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");

    if (lineStart != null && lineStart < 0) {
      logger.severe("La ligne ne peut être négative");
      throw HttpException.badRequest("La ligne ne peut être négative");
    }
    var lineEnd = questionReviewCreateDTO.lineEnd();
    if (lineEnd != null) {
      if (lineEnd < 0) {
        logger.severe("La ligne de fin est inférieur à 0");
        throw HttpException.badRequest("La ligne de fin est inférieur à 0");
      }
      if (lineEnd > fileContent.length) {
        logger.severe("La ligne de fin est supérieur au nombre de ligne total du fichier");
        throw HttpException.badRequest("La ligne de fin est supérieur au nombre de ligne total du fichier");
      }
    }
    var codePart = (questionReviewCreateDTO.lineStart() == null || questionReviewCreateDTO.lineEnd() == null)
        ? null
        : new CodePart(questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd());
    var review = reviewRepository.save(new Review(questionReviewCreateDTO.content(), codePart, new Date()));
    question.addReview(review);
    user.addReview(review);


    String citedCode = null;
    if (lineStart != null && lineEnd != null && lineStart > 0) {
      citedCode = Arrays.stream(fileContent, lineStart - 1, lineEnd).collect(Collectors.joining("\n"));
    }
    tagRepository.addTag(user, review, questionReviewCreateDTO.tagList());
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
    var reviews = List.copyOf(question.getReviews());

    for (var review : reviews) {
      reviewService.removeWithoutUser(review.getId(), user);
    }

    questionVoteRepository.deleteAll(questionVoteRepository.findAllVoteByQuestionId(question.getId()));
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
        question.getTestResult(),
        voteCount,
        question.getReviews().size()
    );
  }

  @Transactional
  public QuestionDetailsWithVotesDTO findByIdWithVotes(Optional<RevevueUserDetail> user, long questionId) {
    var question = questionRepository.findByIdWithAuthorAndReviews(questionId).orElseThrow(() -> HttpException.notFound("Question " + questionId + " does not exists"));
    var dateFormatter = new DateFormatter("dd/MM/yyyy");
    var voteCount = questionVoteRepository.countAllById(questionId);

    return new QuestionDetailsWithVotesDTO(
        question.getId(),
        question.getAuthor().getUsername(),
        dateFormatter.print(question.getCreatedAt(), Locale.FRANCE),
        List.of(),
        question.getTitle(),
        question.getDescription(),
        new String(question.getFile(), StandardCharsets.UTF_8),
        question.getTestFile() != null ? new String(question.getTestFile(), StandardCharsets.UTF_8) : null,
        question.getTestResult(),
        voteCount,
        question.getReviews().size(),
        questionVoteRepository.findUpvoteNumberByQuestionId(questionId),
        questionVoteRepository.findDownvoteNumberByQuestionId(questionId),
        user.map(revevueUserDetail -> questionVoteRepository.findQuestionVote(revevueUserDetail.id(), questionId)).orElse(null)
    );
  }

  @Transactional
  public List<QuestionDTO> getQuestionsFromUserByLogin(String login) {
    var user = userRepository.findByLogin(login).orElseThrow();
    return questionRepository.findByAuthorOrderByCreatedAtDesc(user)
        .stream()
        .map(questionMapper::entityToQuestionDTO)
        .toList();
  }

  @Transactional
  public List<QuestionDTO> getQuestionsFromUser(long userId) {
    var user = userRepository.findById(userId)
        .orElseThrow(() -> HttpException.notFound("user " + userId + " doesn't exist"));
    return questionRepository.findByAuthorOrderByCreatedAtDesc(user)
        .stream()
        .map(questionMapper::entityToQuestionDTO)
        .toList();
  }

  @Transactional
  public List<QuestionDTO> getQuestionsFromFollows(long userId) {
    var questionResult = new ArrayDeque<Question>();
    var userWithFollows = userRepository.findUserWithFollowers(userId).orElseThrow();
    var visitedUsers = new HashSet<Long>();
    var toVisitUsers = new ArrayDeque<>(userWithFollows.getFollows().stream().map(User::getId).toList());
    visitedUsers.add(userId);
    while (!toVisitUsers.isEmpty() || questionResult.size() > 50) {
      var visitScoreFollower = toVisitUsers.poll();
      if (visitScoreFollower == null) break;
      var userOptional = userRepository.findByIdWithQuestions(visitScoreFollower);
      if (userOptional.isEmpty()) {
        continue;
      }
      var user = userOptional.get();

      for (var question : user.getQuestions()) {
        questionResult.push(question);
      }
      user.getFollows()
          .stream()
          .filter(e -> !visitedUsers.contains(e.getId())).forEach(follow -> toVisitUsers.add(follow.getId()));
      visitedUsers.add(user.getId());
    }

    return questionResult
        .stream()
        .map(questionMapper::entityToQuestionDTO)
        .toList();
  }

  @Transactional
  public VoteDTO getVoteOnQuestionById(long questionId) {
    questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("This question doesn't exist"));
    return new VoteDTO(questionVoteRepository.findUpvoteNumberByQuestionId(questionId), questionVoteRepository.findDownvoteNumberByQuestionId(questionId));
  }

  @Transactional
  public void cancelVote(long authorId, long questionId) {
    var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
    var question = questionRepository.findById(questionId).orElseThrow(() -> HttpException.notFound("The question does not exists"));
    var questionVoteId = new QuestionVoteId();
    questionVoteId.setAuthor(user);
    questionVoteId.setQuestion(question);
    questionVoteRepository.deleteById(questionVoteId);
  }

  @Transactional
  public void updateTest(long questionId, String testResult) {
    questionRepository.findById(questionId)
        .ifPresent(question -> {
          question.setTestResult(testResult);
        });
  }

  record UserQuestion(User user, Question question) {
  }

  private record ScoreFollower(long userId, int score) {
  }
}
