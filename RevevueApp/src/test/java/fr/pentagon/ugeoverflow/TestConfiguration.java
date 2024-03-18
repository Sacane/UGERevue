package fr.pentagon.ugeoverflow;

import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan
public class TestConfiguration {

    @Bean
    public UserTestProvider userTestProvider(UserRepository userRepository, PasswordEncoder passwordEncoder, QuestionRepository questionRepository, UserService userService, QuestionService questionService, ReviewRepository reviewRepository, TagRepository tagRepository) {
        return new UserTestProvider(userRepository, passwordEncoder, questionRepository, questionService, userService, reviewRepository, tagRepository);
    }
}
