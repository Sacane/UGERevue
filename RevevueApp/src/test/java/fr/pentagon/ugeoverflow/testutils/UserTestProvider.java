package fr.pentagon.ugeoverflow.testutils;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.model.embed.CodePart;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UserTestProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final TagRepository tagRepository;
    public UserTestProvider (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            QuestionRepository questionRepository, QuestionService questionService, UserService userService,
            ReviewRepository reviewRepository,
            TagRepository tagRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public void addSomeUserIntoDatabase() throws IOException {
        List<User> users = List.of(
                new User("Sacane", "loginSacane", passwordEncoder.encode("SacanePassword"), "sacane.test@gmail.com", Role.USER),
                new User("Sacane2", "loginSacane2", passwordEncoder.encode("SacanePassword2"), "sacane2.test@gmail.com", Role.USER),
                new User("Sacane3", "loginSacane3", passwordEncoder.encode("SacanePassword3"), "sacane3.test@gmail.com", Role.USER),
                new User("Sacane4", "loginSacane4", passwordEncoder.encode("SacanePassword4"), "sacane4.test@gmail.com", Role.USER)
        );
        userRepository.saveAll(users);


        var f1 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorld.java");
        var f2 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldTest.java");

        Question entity = new Question("Hello world ne marche pas chez moi", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity2 = new Question("J'aimerai afficher un Hello world mais j'ai une erreur de compilation", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity3 = new Question("Impossible de trouver le bug", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity4 = new Question("Ce hello world m'exaspere", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity5 = new Question("Je ne comprends pas", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity6 = new Question("Pourquoi est-ce que mon test ne fonctionne pas", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity7 = new Question("J'ai bien peur d'avoir pété Java", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity8 = new Question("J'aimerai optimiser mon code sous Java 21", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());

        entity.setAuthor(users.get(0));
        var user1 = users.get(1);

        entity2.setAuthor(user1);
        entity3.setAuthor(users.get(1));
        entity4.setAuthor(users.get(1));
        var user2 = users.get(2);
        entity5.setAuthor(user2);

        var user3 = users.get(3);
        entity6.setAuthor(user3);
        entity7.setAuthor(user3);
        entity8.setAuthor(user3);

        questionRepository.save(entity);
        questionRepository.save(entity2);
        questionRepository.save(entity3);
        questionRepository.save(entity4);
        questionRepository.save(entity5);
        questionRepository.save(entity6);
        questionRepository.save(entity7);
        questionRepository.save(entity8);

        users.get(0).addQuestion(entity);
        user1.addQuestion(entity3);
        user1.addQuestion(entity4);
        user1.addQuestion(entity2);
        user2.addQuestion(entity5);
        user3.addQuestion(entity6);
        user3.addQuestion(entity7);
        user3.addQuestion(entity8);
    }

    @Transactional
    public void addSomeUserIntoDatabaseWithReview() throws IOException {
        List<User> users = List.of(
                new User("Sacane", "loginSacane", passwordEncoder.encode("SacanePassword"), "sacane.test@gmail.com", Role.USER),
                new User("Sacane2", "loginSacane2", passwordEncoder.encode("SacanePassword2"), "sacane2.test@gmail.com", Role.USER),
                new User("Sacane3", "loginSacane3", passwordEncoder.encode("SacanePassword3"), "sacane3.test@gmail.com", Role.USER),
                new User("Sacane4", "loginSacane4", passwordEncoder.encode("SacanePassword4"), "sacane4.test@gmail.com", Role.USER)
        );
        userRepository.saveAll(users);


        var f1 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorld.java");
        var f2 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldTest.java");

        Question entity = new Question("Hello world ne marche pas chez moi", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity2 = new Question("J'aimerai afficher un Hello world mais j'ai une erreur de compilation", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity3 = new Question("Impossible de trouver le bug", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity4 = new Question("Ce hello world m'exaspere", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity5 = new Question("Je ne comprends pas", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity6 = new Question("Pourquoi est-ce que mon test ne fonctionne pas", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity7 = new Question("J'ai bien peur d'avoir pété Java", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
        Question entity8 = new Question("J'aimerai optimiser mon code sous Java 21", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());

        Review review = new Review("Vraiment nul comme idée", new CodePart(10, 10), new Date());
        Review review1 = new Review("Ping", null, new Date());
        Review reviewOf1 = new Review("Pong", null, new Date());


        entity.setAuthor(users.get(0));
        var user1 = users.get(1);

        entity2.setAuthor(user1);
        entity3.setAuthor(users.get(1));
        entity4.setAuthor(users.get(1));
        var user2 = users.get(2);
        entity5.setAuthor(user2);

        var user3 = users.get(3);
        entity6.setAuthor(user3);
        entity7.setAuthor(user3);
        entity8.setAuthor(user3);

        entity8.addReview(review1);

        questionRepository.save(entity);
        questionRepository.save(entity2);
        questionRepository.save(entity3);
        questionRepository.save(entity4);
        questionRepository.save(entity5);
        questionRepository.save(entity6);
        questionRepository.save(entity7);
        questionRepository.save(entity8);

        users.get(0).addQuestion(entity);
        user1.addQuestion(entity3);
        user1.addQuestion(entity4);
        user1.addQuestion(entity2);
        user2.addQuestion(entity5);
        user3.addQuestion(entity6);
        user3.addQuestion(entity7);
        user3.addQuestion(entity8);

        review.setAuthor(user1);
        reviewRepository.save(review);
        user1.addReview(review);

        review1.setAuthor(user2);
        var javaTag = new Tag("Java");
        user2.addTag(javaTag);
        tagRepository.save(javaTag);
        review1.addTag(javaTag);
        reviewRepository.save(review1);
        user2.addReview(review1);

        reviewOf1.setAuthor(user3);
        var tag = new Tag("Obscure");
        var tag1 = new Tag("RequireNull");
        user3.addTag(tag);
        user3.addTag(tag1);
        tagRepository.save(tag);
        reviewOf1.addTag(tag);
        tagRepository.save(tag1);
        reviewOf1.addTag(tag1);
        review1.addReview(reviewOf1);
        reviewRepository.save(reviewOf1);
        user3.addReview(reviewOf1);

    }

    @Transactional
    public void hugeSetup() {
        var random = new Random();
        for(var i = 0; i < 10000; i++) {
            var username = "userNumber#" + i;
            var password = passwordEncoder.encode("pwd#" + i);
            var email = "userNumber#" + i + "@gmail.com";
            var role = Role.USER;
            User user = new User(username, username, password, email, role);
            userRepository.save(user);
            provideQuestions(user);
        }
        for(var i = 0; i < 10000; i++) {
            var follower = random.nextLong(0, 10000);
            var followed = random.nextLong(0, 10000);
            while(followed == follower){
                follower = random.nextLong(0, 10000);
                followed = random.nextLong(0, 10000);
            }
            userService.follow(follower, followed);
        }
    }
    private void provideQuestions(User user) {
        for(var i = 0; i < 1000; i++) {
            questionService.create(new NewQuestionDTO("My question number #" + i, "description of my question number #" + i, new byte[]{}, null, "test.java", null), user.getId());
        }
    }
}
